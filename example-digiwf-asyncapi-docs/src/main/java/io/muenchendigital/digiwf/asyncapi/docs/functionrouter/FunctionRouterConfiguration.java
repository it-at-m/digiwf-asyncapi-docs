package io.muenchendigital.digiwf.asyncapi.docs.functionrouter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.function.context.MessageRoutingCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is not a working example. It's for demonstration purposes only!
 */
@Configuration
public class FunctionRouterConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MessageRoutingCallback customRouter() {
        return null;
    }
}
