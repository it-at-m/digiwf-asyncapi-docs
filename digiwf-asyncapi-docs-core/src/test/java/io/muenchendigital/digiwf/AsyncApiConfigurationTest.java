package io.muenchendigital.digiwf;

import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import io.github.stavshamir.springwolf.schemas.DefaultSchemasService;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AsyncApiConfigurationTest {

    private final SchemasService schemasService = new DefaultSchemasService();
    private final String basePackage = "io.muenchendigital.digiwf";
    private final String binder = "kafka";
    private final String broker = "localhost:9092";
    private final String version = "1.0.0";
    private final String title= "Kafka Test";

    @Test
    void asyncApiDocket() {
        final AsyncApiConfiguration asyncApiConfiguration = new AsyncApiConfiguration(
                this.binder,
                this.broker,
                this.basePackage,
                this.version,
                this.title
        );
        final AsyncApiDocket docket = asyncApiConfiguration.asyncApiDocket();

        Assertions.assertEquals(this.basePackage, docket.getBasePackage());
        Assertions.assertEquals(this.broker, docket.getServers().get("kafka").getUrl());
        Assertions.assertEquals(this.binder, docket.getServers().get("kafka").getProtocol());
        Assertions.assertEquals(this.version, docket.getInfo().getVersion());
        Assertions.assertEquals(this.title, docket.getInfo().getTitle());
    }

}
