package io.muenchendigital.digiwf.asyncapi.docs;

import io.muenchendigital.digiwf.asyncapi.docs.annotations.DocumentAsyncAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

@Slf4j
public class Consumer {

    @DocumentAsyncAPI(payload = Payload.class)
    public java.util.function.Consumer<Message<Payload>> receiveMessage() {
        return message -> {
            log.info(message.getPayload().getMessage());
        };
    }

    @DocumentAsyncAPI(payload = Payload.class)
    public java.util.function.Consumer<Message<Payload>> receiveAnotherMessage() {
        return message -> {
            log.info(message.getPayload().getMessage());
        };
    }

}
