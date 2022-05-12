package io.muenchendigital.digiwf.producer;

import io.muenchendigital.digiwf.DocumentAsyncAPI;
import io.muenchendigital.digiwf.dto.DeploymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@DocumentAsyncAPI(payload = DeploymentEvent.class)
@Slf4j
@Configuration
public class DeploymentProducerConfig {

    @Bean
    public Sinks.Many<Message<DeploymentEvent>> deploymentSink() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Supplier<Flux<Message<DeploymentEvent>>> deployArtifact(final Sinks.Many<Message<DeploymentEvent>> sink) {
        return sink::asFlux;
    }
}
