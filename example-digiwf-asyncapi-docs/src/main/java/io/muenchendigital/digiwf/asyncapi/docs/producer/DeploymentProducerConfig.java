package io.muenchendigital.digiwf.asyncapi.docs.producer;

import io.muenchendigital.digiwf.asyncapi.docs.annotations.DocumentAsyncAPI;
import io.muenchendigital.digiwf.asyncapi.docs.dto.DeploymentEvent;
import io.muenchendigital.digiwf.asyncapi.docs.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Slf4j
@Configuration
public class DeploymentProducerConfig {

    @Bean
    public Sinks.Many<Message<DeploymentEvent>> deploymentSink() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @DocumentAsyncAPI(payload = MessageDto.class)
    @Bean
    public Supplier<Flux<Message<DeploymentEvent>>> deployArtifact(final Sinks.Many<Message<DeploymentEvent>> sink) {
        return sink::asFlux;
    }
}
