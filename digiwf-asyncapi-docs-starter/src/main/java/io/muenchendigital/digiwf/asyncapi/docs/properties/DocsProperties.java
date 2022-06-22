package io.muenchendigital.digiwf.asyncapi.docs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "io.muenchendigital.digiwf.docs")
public class DocsProperties {

    private String basePackage;
    private String version;
    private String title;

}
