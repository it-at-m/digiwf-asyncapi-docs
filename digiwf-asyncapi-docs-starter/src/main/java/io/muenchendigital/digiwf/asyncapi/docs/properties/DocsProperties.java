package io.muenchendigital.digiwf.asyncapi.docs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "io.muenchendigital.digiwf.docs")
public class DocsProperties {
    private String basePackage;
    private String version;
    private String title;
}
