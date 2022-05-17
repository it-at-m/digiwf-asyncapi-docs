package io.muenchendigital.digiwf.configuration;

import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import io.github.stavshamir.springwolf.configuration.EnableAsyncApi;
import io.github.stavshamir.springwolf.schemas.DefaultSchemasService;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import io.muenchendigital.digiwf.AsyncApiConfiguration;
import io.muenchendigital.digiwf.SpringCloudStreamChannelScanner;
import io.muenchendigital.digiwf.properties.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@EnableAsyncApi
@RequiredArgsConstructor
@Configuration
public class AsyncAPIDocsAutoConfiguration {

    private final BindingProperties bindingProperties;
    private final DefinitionProperties definitionProperties;
    private final DocsProperties docsProperties;
    private final FunctionRouterProperties functionRouterProperties;
    private final KafkaProperties kafkaProperties;

    @Bean
    @ConditionalOnMissingBean
    public SchemasService schemasService() {
        return new DefaultSchemasService();
    }

    @Bean
    public SpringCloudStreamChannelScanner springCloudStreamChannelScanner(final SchemasService schemasService) {
        return new SpringCloudStreamChannelScanner(
                schemasService,
                this.definitionProperties.getDefinitions(),
                this.bindingProperties.getBindings(),
                this.docsProperties.getBasePackage(),
                this.functionRouterProperties.isEnabled()
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
