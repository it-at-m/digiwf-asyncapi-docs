# asyncapi-docs-starter documentation

[Springwolf](https://springwolf.github.io/) is a library that auto-generate documentation of async APIs.
It currently does not support spring cloud stream apis. 
Therefore, this library provides a custom implementation of a `ChannelsScanner` that auto-generates documentation based on 
the spring cloud stream consumer and producer properties configured in application.properties.

## Usage

This library will automatically generate documentations for the channels you configured.
Therefore, configure spring cloud stream consumer and producer functions as you are used to. And annotate the classes which contain the spring cloud function `@bean`s with the `@DocumentAsyncAPI(payload = YourPayloadClass.class)`.
The annotation `@DocumentAsyncAPI(payload = YourPayloadClass.class)` is used to determine the payload type of  the events.
Additionally, you should add a `io.muenchendigital.digiwf.basePackage` property to your application.properties file with the base package of your application. For example:

```
io.muenchendigital.digiwf.basePackage=io.muenchendigital.digiwf
```

### Example for consumers

1. Add properties to application.properties

```
# default binder
spring.cloud.stream.default-binder=kafka
# the kafka server
spring.cloud.stream.kafka.binder.brokers=localhost:9092
# cloud functions
spring.cloud.function.definition=receiveMessage
# channel binding
spring.cloud.stream.bindings.receiveMessage-in-0.destination=kafka-demo-receive-message,kafka-demo-test1
spring.cloud.stream.bindings.receiveMessage-in-0.group=kafka-demo

# required base package
io.muenchendigital.digiwf.basePackage=io.muenchendigital.digiwf
```

2. Create the consumer

```java
@DocumentAsyncAPI(payload = MessageDto.class)
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConsumerConfiguration {

    @Bean
    public Consumer<Message<MessageDto>> receiveMessage() {
        return message -> {
            log.info(message.getPayload().getMessage());
        };
    }

}
```

3. Create a springwolf configuration class. See [springwolfs documentation](https://springwolf.github.io/docs/quickstart#configuration-class)

4. Check out the documentation at [http://localhost:8080/springwolf/asyncapi-ui.html](http://localhost:8080/springwolf/asyncapi-ui.html)

### Example for producers

1. Add properties to application.property

```
# default binder
spring.cloud.stream.default-binder=kafka
# the kafka server
spring.cloud.stream.kafka.binder.brokers=localhost:9092
# cloud functions
spring.cloud.function.definition=sendMessage
# channel binding
spring.cloud.stream.bindings.sendMessage-out-0.destination=kafka-demo-send-message

# required base package
io.muenchendigital.digiwf.basePackage=io.muenchendigital.digiwf
```

2. Create the producer

```java
@DocumentAsyncAPI(payload = MessageDto.class)
@Slf4j
@Configuration
public class ProducerConfig {

    @Bean
    public Sinks.Many<Message<MessageDto>> createMessageSink() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Supplier<Flux<Message<MessageDto>>> sendMessage(final Sinks.Many<Message<MessageDto>> messagePublisher) {
        return messagePublisher::asFlux;
    }
}
```

3. Create a springwolf configuration class. See [springwolfs documentation](https://springwolf.github.io/docs/quickstart#configuration-class)

4. Check out the documentation at [http://localhost:8080/springwolf/asyncapi-ui.html](http://localhost:8080/springwolf/asyncapi-ui.html)


## Limitations

- **Function routers**: This library cannot track producers that use function routing (`spring.cloud.stream.sendto.destination` header). If you use this feature of spring cloud stream you have to manually declare the producers according to [springwolfs documentation](https://springwolf.github.io/docs/documenting-producers).
- **Kafka headers**: Kafka headers are currently not supported by springwolf. After this library ist built on springwolf it also does not support kafka headers.

## Example App

The example app [example-digiwf-asyncapi-docs](../example-digiwf-asyncapi-docs) shows basic configuration of consumers and producers
that are automatically documented with the digiwf-asyncapi-docs-starter.

1. Get kafka running

```
# You can use the docker-compose.yml in the projects root folder
docker-compose up

# Or you have to install kafka locally
```

2. Build and start the example app

```
mvn clean install
mvn spring-boot:run
```

3. Check out the documentation at [http://localhost:8080/springwolf/asyncapi-ui.html](http://localhost:8080/springwolf/asyncapi-ui.html)
