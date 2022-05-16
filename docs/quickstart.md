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

3. Add your preferred binder (see [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream)). In this
   example, we use kafka.

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

5. Setup the base package of your application in the application.properties. For example

```
# required base package
io.muenchendigital.digiwf.basePackage=io.muenchendigital.digiwf
```

6. Annotate your ConsumerConfiguration Classes with `@DocumentAsyncAPI(payload = YourPayloadClass.class)`

7. Create a springwolf configuration class. See [springwolfs documentation](https://springwolf.github.io/docs/quickstart#configuration-class)

8. Check out the documentation at [http://localhost:8080/springwolf/asyncapi-ui.html](http://localhost:8080/springwolf/asyncapi-ui.html)

**For more information and code examples see [documentation.md](documentation.md)**
