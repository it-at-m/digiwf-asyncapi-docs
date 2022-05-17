# Getting started

Below is an example of how you can install and set up your service.

1. Use the spring initalizer and create a Spring Boot application with `Spring Cloud Stream` dependencies
[https://start.spring.io](https://start.spring.io)
2. Add the digiwf-asyncapi-docs-starter dependency.

With Maven:

```
<dependency>
     <groupId>io.muenchendigital.digiwf</groupId>
     <artifactId>digiwf-asyncapi-docs-starter</artifactId>
     <version>${digiwf.version}</version>
</dependency>
```

With Gradle:

```
implementation group: 'io.muenchendigital.digiwf', name: 'digiwf-asyncapi-docs-starter', version: '${digiwf.version}'
```

3. Add the kafka binder (see [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream)).

Maven:

 ```
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-stream-binder-kafka</artifactId>
</dependency>
```

Gradle:

```
implementation group: 'org.springframework.cloud', name: 'spring-cloud-stream-binder-kafka'
```

4. Add properties for spring cloud stream consumers and producers to application.properties

```
spring.cloud.stream.default-binder=kafka
spring.cloud.stream.kafka.binder.brokers=localhost:9092
spring.cloud.function.definition=receiveMessage
spring.cloud.stream.bindings.receiveMessage-in-0.destination=kafka-demo-receive-message,kafka-demo-test1
spring.cloud.stream.bindings.receiveMessage-in-0.group=kafka-demo
```

5. Setup the base package, version and title of docs for your application in the application.properties. For example

```
io.muenchendigital.digiwf.docs.basePackage=io.muenchendigital.digiwf
io.muenchendigital.digiwf.docs.version=1.0.0
io.muenchendigital.digiwf.docs.title=kafka-example
```

6. Annotate your ConsumerConfiguration Classes with `@DocumentAsyncAPI(payload = YourPayloadClass.class)`

7. Check out the documentation at [http://localhost:8080/springwolf/asyncapi-ui.html](http://localhost:8080/springwolf/asyncapi-ui.html)

**For more information and code examples see [documentation.md](documentation.md)**
