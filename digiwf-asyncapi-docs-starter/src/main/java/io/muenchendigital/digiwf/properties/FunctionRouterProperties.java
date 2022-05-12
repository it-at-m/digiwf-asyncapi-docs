package io.muenchendigital.digiwf.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.cloud.stream.function.routing")
public class FunctionRouterProperties {
    private boolean enabled = false;
}
