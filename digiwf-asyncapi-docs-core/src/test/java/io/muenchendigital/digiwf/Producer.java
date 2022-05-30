package io.muenchendigital.digiwf;

import io.muenchendigital.digiwf.annotations.DocumentAsyncAPI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Producer {

    @DocumentAsyncAPI(payload = Payload.class)
    public Payload sendMessage() {
        return null;
    }

}
