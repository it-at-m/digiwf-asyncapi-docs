package io.muenchendigital.digiwf.configuration;

import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import io.github.stavshamir.springwolf.configuration.EnableAsyncApi;
import io.github.stavshamir.springwolf.schemas.DefaultSchemasService;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import io.muenchendigital.digiwf.AsyncApiConfiguration;
import io.muenchendigital.digiwf.properties.BindingProperties;
import io.muenchendigital.digiwf.properties.DefinitionProperties;
import io.muenchendigital.digiwf.properties.DocsProperties;
import io.muenchendigital.digiwf.properties.KafkaProperties;
import io.muenchendigital.digiwf.scanners.ConsumerAndProducerScanner;
import io.muenchendigital.digiwf.scanners.FunctionRouterScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@EnableAsyncApi
@RequiredArgsConstructor
@Configuration
public class AsyncAPIDocsAutoConfiguration {

    private final BindingProperties bindingProperties;
    private final DefinitionProperties definitionProperties;
    private final DocsProperties docsProperties;
    private final KafkaProperties kafkaProperties;

    @Bean
    @ConditionalOnMissingBean
    public SchemasService schemasService() {
        return new DefaultSchemasService();
    }

    @Bean
    public ConsumerAndProducerScanner consumerAndProducerScanner(final SchemasService schemasService) {
        return new ConsumerAndProducerScanner(
                schemasService,
                this.definitionProperties.getDefinitions(),
                this.bindingProperties.getBindings(),
                this.docsProperties.getBasePackage()
        );
    }

    @Bean
    @ConditionalOnProperty(name = "spring.cloud.stream.function.routing.enabled")
    public FunctionRouterScanner functionRouterScanner(final SchemasService schemasService) {
        return new FunctionRouterScanner(
                schemasService,
                this.definitionProperties.getDefinitions(),
                this.bindingProperties.getBindings(),
                this.docsProperties.getBasePackage()
        );
    }

    @Bean
    public AsyncApiConfiguration asyncApiConfiguration() {
        return new AsyncApiConfiguration(
                this.kafkaProperties.getDefaultBinder(),
                this.kafkaProperties.getBroker(),
                this.docsProperties.getBasePackage(),
                this.docsProperties.getVersion(),
                this.docsProperties.getTitle()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public AsyncApiDocket asyncApiDocket(final AsyncApiConfiguration asyncApiConfiguration) {
        return asyncApiConfiguration.asyncApiDocket();
    }

}
