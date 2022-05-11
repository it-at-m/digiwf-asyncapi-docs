package io.muenchendigital.digiwf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

@DocumentAsyncAPI(payload = Payload.class)
@Slf4j
public class Consumer {

    public java.util.function.Consumer<Message<Payload>> receiveMessage() {
        return message -> {
            log.info(message.getPayload().getMessage());
        };
    }

    public java.util.function.Consumer<Message<Payload>> receiveAnotherMessage() {
        return message -> {
            log.info(message.getPayload().getMessage());
        };
    }

}
