package io.muenchendigital.digiwf.asyncapi.docs.configuration;

import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import io.github.stavshamir.springwolf.configuration.EnableAsyncApi;
import io.github.stavshamir.springwolf.schemas.DefaultSchemasService;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import io.muenchendigital.digiwf.asyncapi.docs.AsyncApiConfiguration;
import io.muenchendigital.digiwf.asyncapi.docs.properties.BindingProperties;
import io.muenchendigital.digiwf.asyncapi.docs.properties.DocsProperties;
import io.muenchendigital.digiwf.asyncapi.docs.scanners.ConsumerAndProducerScanner;
import io.muenchendigital.digiwf.asyncapi.docs.scanners.FunctionRouterScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@EnableAsyncApi
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties({BindingProperties.class, DocsProperties.class})
public class AsyncAPIDocsAutoConfiguration {

    private final BindingProperties bindingProperties;
    private final DocsProperties docsProperties;
    @Value("${spring.cloud.stream.default-binder}")
    private String defaultBinder;
    @Value("${spring.cloud.stream.kafka.binder.brokers}")
    private String broker;
    @Value("#{'${spring.cloud.function.definition}'.split(';')}")
    private List<String> definitions;

    @Bean
    @ConditionalOnMissingBean
    public SchemasService schemasService() {
        return new DefaultSchemasService();
    }

    @Bean
    public ConsumerAndProducerScanner consumerAndProducerScanner(final SchemasService schemasService) {
        return new ConsumerAndProducerScanner(
                schemasService,
                this.definitions,
                this.bindingProperties.getBindings(),
                this.docsProperties.getBasePackage()
        );
    }

    @Bean
    @ConditionalOnProperty(name = "spring.cloud.stream.function.routing.enabled")
    public FunctionRouterScanner functionRouterScanner(final SchemasService schemasService) {
        return new FunctionRouterScanner(
                schemasService,
                this.definitions,
                this.bindingProperties.getBindings(),
                this.docsProperties.getBasePackage()
        );
    }

    @Bean
    public AsyncApiConfiguration asyncApiConfiguration() {
        return new AsyncApiConfiguration(
                this.defaultBinder,
                this.broker,
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
