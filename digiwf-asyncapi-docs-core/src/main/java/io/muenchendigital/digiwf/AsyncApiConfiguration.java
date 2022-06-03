package io.muenchendigital.digiwf;

import com.asyncapi.v2.model.info.Info;
import com.asyncapi.v2.model.server.Server;
import io.github.stavshamir.springwolf.configuration.AsyncApiDocket;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AsyncApiConfiguration {

    private String binder;
    private String broker;
    private String basePackage;
    private String version;
    private String title;

    public AsyncApiDocket asyncApiDocket() {
        final Info info = Info.builder()
                .version(this.version)
                .title(this.title)
                .build();

        final Server server = Server.builder()
                .protocol(this.binder)
                .url(this.broker)
                .build();

        // registering producers is not required. Producers and consumers are automatically detected.
        return AsyncApiDocket.builder()
                .basePackage(this.basePackage)
                .info(info)
                .server(this.binder, server)
                .build();
    }

}
