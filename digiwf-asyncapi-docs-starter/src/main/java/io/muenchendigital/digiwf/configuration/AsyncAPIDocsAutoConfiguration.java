package io.muenchendigital.digiwf.configuration;

import io.github.stavshamir.springwolf.schemas.SchemasService;
import io.muenchendigital.digiwf.SpringCloudStreamChannelScanner;
import io.muenchendigital.digiwf.properties.BindingProperties;
import io.muenchendigital.digiwf.properties.DefinitionProperties;
import io.muenchendigital.digiwf.properties.PackageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@Configuration
public class AsyncAPIDocsAutoConfiguration {

    private final SchemasService schemasService;
    private final BindingProperties bindingProperties;
    private final DefinitionProperties definitionProperties;
    private final PackageProperties packageProperties;


    @Bean
    public SpringCloudStreamChannelScanner springCloudStreamChannelScanner() {
        return new SpringCloudStreamChannelScanner(
                this.schemasService,
                this.definitionProperties.getDefinitions(),
                this.bindingProperties.getBindings(),
                this.packageProperties.getBasePackage()
        );
    }

}
