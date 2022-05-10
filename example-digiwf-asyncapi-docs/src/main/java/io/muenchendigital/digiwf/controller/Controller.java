package io.muenchendigital.digiwf.controller;

import io.muenchendigital.digiwf.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
@RequestMapping("/api/message")
@RestController
public class Controller {
    private final Sinks.Many<Message<MessageDto>> messageProducer;

    @PostMapping
    public MessageDto sendMessage(@RequestBody MessageDto message) {
        final Message<MessageDto> msg = MessageBuilder.withPayload(message).build();
        this.messageProducer.tryEmitNext(msg);
        return message;
    }
}
