package io.muenchendigital.digiwf.asyncapi.docs;

import io.muenchendigital.digiwf.asyncapi.docs.annotations.DocumentAsyncAPI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Producer {

    @DocumentAsyncAPI(payload = Payload.class)
    public Payload sendMessage() {
        return null;
    }

}
