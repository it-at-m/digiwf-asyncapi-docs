package io.muenchendigital.digiwf;

@DocumentAsyncAPI(payload = Payload.class)
public class FunctionRouter {

    public Object customRouter(final Payload payload) {
        // some custom router implementation
        return null;
    }

}
