package io.muenchendigital.digiwf.asyncapi.docs.consumer;

import io.muenchendigital.digiwf.asyncapi.docs.annotations.DocumentAsyncAPI;
import io.muenchendigital.digiwf.asyncapi.docs.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConsumerConfiguration {

    @DocumentAsyncAPI(payload = MessageDto.class)
    @Bean
    public Consumer<Message<MessageDto>> receiveMessage() {
        return message -> {
            log.info(message.getPayload().getMessage());
        };
    }

}
