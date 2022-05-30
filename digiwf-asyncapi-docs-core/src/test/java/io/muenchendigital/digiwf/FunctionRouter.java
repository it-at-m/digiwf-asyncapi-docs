package io.muenchendigital.digiwf;

public class FunctionRouter {

    @DocumentAsyncAPI(payload = Payload.class)
    public Object customRouter(final Payload payload) {
        // some custom router implementation
        return null;
    }

}
