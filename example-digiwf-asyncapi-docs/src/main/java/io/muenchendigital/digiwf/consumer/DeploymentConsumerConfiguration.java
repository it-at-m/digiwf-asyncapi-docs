package io.muenchendigital.digiwf.consumer;

import io.muenchendigital.digiwf.DocumentAsyncAPI;
import io.muenchendigital.digiwf.dto.DeploymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@DocumentAsyncAPI(payload = DeploymentEvent.class)
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeploymentConsumerConfiguration {

    @Bean
    public Consumer<Message<DeploymentEvent>> deployments() {
        return message -> {
            log.info(message.getPayload().getDeploymentId());
        };
    }

}
