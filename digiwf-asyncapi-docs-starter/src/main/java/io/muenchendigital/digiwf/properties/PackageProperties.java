package io.muenchendigital.digiwf.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "io.muenchendigital.digiwf")
public class PackageProperties {
    private String basePackage;
}
