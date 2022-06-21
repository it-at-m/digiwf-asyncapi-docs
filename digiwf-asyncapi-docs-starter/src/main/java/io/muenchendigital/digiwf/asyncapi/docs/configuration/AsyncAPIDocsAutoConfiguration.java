package io.muenchendigital.digiwf.asyncapi.docs.configuration;

import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import io.github.stavshamir.springwolf.configuration.EnableAsyncApi;
import io.github.stavshamir.springwolf.schemas.DefaultSchemasService;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import io.muenchendigital.digiwf.asyncapi.docs.AsyncApiConfiguration;
import io.muenchendigital.digiwf.asyncapi.docs.properties.BindingProperties;
import io.muenchendigital.digiwf.asyncapi.docs.properties.DefinitionProperties;
import io.muenchendigital.digiwf.asyncapi.docs.properties.DocsProperties;
import io.muenchendigital.digiwf.asyncapi.docs.properties.KafkaProperties;
import io.muenchendigital.digiwf.asyncapi.docs.scanners.ConsumerAndProducerScanner;
import io.muenchendigital.digiwf.asyncapi.docs.scanners.FunctionRouterScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@EnableAsyncApi
@RequiredArgsConstructor
@Configuration
@ComponentScan(basePackages = "io.muenchendigital.digiwf.asyncapi.docs")
@EnableConfigurationProperties({BindingProperties.class, DefinitionProperties.class, DocsProperties.class, KafkaProperties.class})
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
