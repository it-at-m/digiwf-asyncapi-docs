package io.muenchendigital.digiwf.asyncapi.docs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
public class KafkaProperties {

    @Value("${spring.cloud.stream.default-binder}")
    private String defaultBinder;

    @Value("${spring.cloud.stream.kafka.binder.brokers}")
    private String broker;

}
