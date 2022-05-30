package io.muenchendigital.digiwf.functionrouter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.function.context.MessageRoutingCallback;
import org.springframework.context.annotation.Bean;

public class FunctionRouterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MessageRoutingCallback customRouter() {
        return null;
    }
}
