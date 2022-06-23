package io.muenchendigital.digiwf.asyncapi.docs.functionrouter;

import io.muenchendigital.digiwf.asyncapi.docs.annotations.DocumentAsyncAPI;
import io.muenchendigital.digiwf.asyncapi.docs.dto.DeploymentEvent;
import io.muenchendigital.digiwf.asyncapi.docs.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class FunctionRouterConsumers {

    @DocumentAsyncAPI(payload = MessageDto.class, functionRouter = true, typeHeader = "functionRouterReceiveMessage")
    @Bean
    public Consumer<Message<MessageDto>> functionRouterReceiveMessage() {
        return message -> {
            log.info(message.getPayload().getMessage());
        };
    }

    @DocumentAsyncAPI(payload = DeploymentEvent.class, functionRouter = true, typeHeader = "functionRouterReceiveAnotherMessage")
    @Bean
    public Consumer<Message<DeploymentEvent>> functionRouterDeployments() {
        return message -> {
            log.info(message.getPayload().getDeploymentId());
        };
    }
}
