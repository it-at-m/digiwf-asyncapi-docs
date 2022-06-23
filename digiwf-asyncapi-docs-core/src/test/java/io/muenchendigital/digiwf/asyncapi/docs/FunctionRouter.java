package io.muenchendigital.digiwf.asyncapi.docs;

import io.muenchendigital.digiwf.asyncapi.docs.annotations.DocumentAsyncAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

@Slf4j
public class FunctionRouter {

    public Object customRouter() {
        // some custom router implementation
        return null;
    }

    @DocumentAsyncAPI(payload = Payload.class, functionRouter = true, typeHeader = "receiveMessage")
    public java.util.function.Consumer<Message<Payload>> functionRouterReceiveMessage() {
        return message -> {
            log.info(message.getPayload().getMessage());
        };
    }

    @DocumentAsyncAPI(payload = Object.class, functionRouter = true, typeHeader = "receiveAnotherMessage")
    public java.util.function.Consumer<Message<Object>> functionRouterReceiveAnotherMessage() {
        return message -> {
            log.info(message.getPayload().toString());
        };
    }

}
