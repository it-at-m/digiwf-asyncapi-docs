package io.muenchendigital.digiwf;

import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AsyncApiConfigurationTest {

    @Test
    void asyncApiDocket() {

        final String basePackage = "io.muenchendigital.digiwf";
        final String binder = "kafka";
        final String broker = "localhost:9092";
        final String version = "1.0.0";
        final String title = "Kafka Test";

        final AsyncApiConfiguration asyncApiConfiguration = new AsyncApiConfiguration(
                binder,
                broker,
                basePackage,
                version,
                title
        );
        final AsyncApiDocket docket = asyncApiConfiguration.asyncApiDocket();

        Assertions.assertEquals(basePackage, docket.getBasePackage());
        Assertions.assertEquals(broker, docket.getServers().get("kafka").getUrl());
        Assertions.assertEquals(binder, docket.getServers().get("kafka").getProtocol());
        Assertions.assertEquals(version, docket.getInfo().getVersion());
        Assertions.assertEquals(title, docket.getInfo().getTitle());
    }

}
