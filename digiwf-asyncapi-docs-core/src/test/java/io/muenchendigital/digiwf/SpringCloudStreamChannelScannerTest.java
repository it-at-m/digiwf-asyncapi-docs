package io.muenchendigital.digiwf;


import com.asyncapi.v2.binding.OperationBinding;
import com.asyncapi.v2.binding.kafka.KafkaOperationBinding;
import com.asyncapi.v2.model.channel.ChannelItem;
import com.asyncapi.v2.model.channel.operation.Operation;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.message.Message;
import io.github.stavshamir.springwolf.schemas.DefaultSchemasService;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import io.muenchendigital.digiwf.scanners.ConsumerAndProducerScanner;
import io.muenchendigital.digiwf.scanners.FunctionRouterScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SpringCloudStreamChannelScannerTest {

    private final SchemasService schemasService = new DefaultSchemasService();
    private final String basePackage = "io.muenchendigital.digiwf";

    @Test
    void scannerScanConsumersTest() {
        final String groupId = "simpleConsumers";

        // props
        final List<String> definitions = List.of("receiveMessage", "receiveAnotherMessage");
        final Map<String, Map<String, String>> bindings = Map.of(
                "receiveMessage-in-0", Map.of("group", groupId, "destination", "kafka-demo-receive-message"),
                "receiveAnotherMessage-in-0", Map.of("group", groupId, "destination", "kafka-demo-receive-another-message")
        );

        final ConsumerAndProducerScanner scanner = new ConsumerAndProducerScanner(this.schemasService, definitions, bindings, this.basePackage);
        final Map<String, ChannelItem> channels = scanner.scan();

        Assertions.assertEquals(2, channels.keySet().size());

        channels.keySet().forEach(channelKey -> {
            // verify that channelKey == destination
            Assertions.assertTrue(bindings
                    .values()
                    .stream()
                    .anyMatch(binding -> binding.get("destination").equals(channelKey)));

            final ChannelItem channel = channels.get(channelKey);
            final Operation operation = channel.getPublish();

            this.verifyOperationMessagePayload((Message) operation.getMessage(), Payload.class);
            this.verifyOperationBindings((Map<String, OperationBinding>) operation.getBindings(), groupId, channelKey);
        });
    }

    @Test
    void scannerScanConsumersListenToMultipleStreamsTest() {
        final String groupId = "simpleConsumers";

        // props
        final List<String> definitions = List.of("receiveMessage");
        final Map<String, Map<String, String>> bindings = Map.of(
                "receiveMessage-in-0", Map.of("group", groupId, "destination", "kafka-demo-receive-message,kafka-demo-receive-another-message")
        );

        final ConsumerAndProducerScanner scanner = new ConsumerAndProducerScanner(this.schemasService, definitions, bindings, this.basePackage);
        final Map<String, ChannelItem> channels = scanner.scan();

        Assertions.assertEquals(1, channels.keySet().size());

        channels.keySet().forEach(channelKey -> {
            // verify that channelKey == destination
            Assertions.assertTrue(bindings
                    .values()
                    .stream()
                    .anyMatch(binding -> binding.get("destination").equals(channelKey)));

            final ChannelItem channel = channels.get(channelKey);
            final Operation operation = channel.getPublish();

            this.verifyOperationMessagePayload((Message) operation.getMessage(), Payload.class);
            this.verifyOperationBindings((Map<String, OperationBinding>) operation.getBindings(), groupId, List.of(channelKey.split(",")));
        });
    }

    @Test
    void scannerScanProducerTest() {
        // props
        final List<String> definitions = List.of("sendMessage");
        final Map<String, Map<String, String>> bindings = Map.of(
                "sendMessage-out-0", Map.of("destination", "kafka-demo-send-message")
        );

        final ConsumerAndProducerScanner scanner = new ConsumerAndProducerScanner(this.schemasService, definitions, bindings, this.basePackage);
        final Map<String, ChannelItem> channels = scanner.scan();

        Assertions.assertEquals(1, channels.keySet().size());

        channels.keySet().forEach(channelKey -> {
            // verify that channelKey == destination
            Assertions.assertTrue(bindings
                    .values()
                    .stream()
                    .anyMatch(binding -> binding.get("destination").equals(channelKey)));

            final ChannelItem channel = channels.get(channelKey);
            final Operation operation = channel.getSubscribe();

            this.verifyOperationMessagePayload((Message) operation.getMessage(), Payload.class);
            this.verifyOperationBindings((Map<String, OperationBinding>) operation.getBindings(), channelKey);
        });
    }

    @Test
    void scannerScanConsumersWithFunctionRouterTest() {
        final String groupId = "functionRouterConsumers";

        // props
        final List<String> definitions = List.of("functionRouter");
        final Map<String, Map<String, String>> bindings = Map.of(
                "functionRouter-in-0", Map.of("group", groupId, "destination", "kafka-demo-function-router-in")
        );
        final List<String> typeHeaders = List.of("receiveMessage", "receiveAnotherMessage");

        final FunctionRouterScanner scanner = new FunctionRouterScanner(this.schemasService, definitions, bindings, this.basePackage);
        final Map<String, ChannelItem> channels = scanner.scan();

        Assertions.assertEquals(2, channels.keySet().size());

        channels.keySet().forEach(channelKey -> {
            final Optional<String> type = typeHeaders
                    .stream()
                    .filter(typeHeader -> channelKey.contains(typeHeader))
                    .findAny();
            Assertions.assertTrue(type.isPresent());

            final String destination = channelKey.split(type.get())[1].replace(": ", "");

            // verify that channelKey == destination
            Assertions.assertTrue(bindings
                    .values()
                    .stream()
                    .anyMatch(binding -> binding.get("destination").equals(destination)));

            final ChannelItem channel = channels.get(channelKey);
            final Operation operation = channel.getPublish();

            final Message msg = (Message) operation.getMessage();
            Assertions.assertTrue(msg.getName().equals(Payload.class.getName()) || msg.getName().equals(Object.class.getName()));
            Assertions.assertTrue(msg.getTitle().equals(Payload.class.getSimpleName()) || msg.getTitle().equals(Object.class.getSimpleName()));
            Assertions.assertTrue(msg.getPayload().get$ref().equals("#/components/schemas/" + Payload.class.getSimpleName()) || msg.getPayload().get$ref().equals("#/components/schemas/" + Object.class.getSimpleName()));

            this.verifyOperationBindings((Map<String, OperationBinding>) operation.getBindings(), destination);
        });
    }

    private void verifyOperationMessagePayload(final Message msg, final Class<?> messagePayload) {
        Assertions.assertEquals(messagePayload.getName(), msg.getName());
        Assertions.assertEquals(messagePayload.getSimpleName(), msg.getTitle());
        Assertions.assertEquals("#/components/schemas/" + messagePayload.getSimpleName(), msg.getPayload().get$ref());
    }

    private void verifyOperationBindings(final Map<String, OperationBinding> bindingsMap, final String groupId, final String destination) {
        this.verifyOperationBindings(bindingsMap, groupId, List.of(destination));
    }

    private void verifyOperationBindings(final Map<String, OperationBinding> bindingsMap, final String groupId, final List<String> destinations) {
        destinations.forEach(destination -> {
            this.verifyOperationBindings(bindingsMap, destination);
            final KafkaOperationBinding kafkaOperationBinding = (KafkaOperationBinding) bindingsMap.get(destination);
            Assertions.assertEquals(groupId, kafkaOperationBinding.getGroupId());
        });
    }

    private void verifyOperationBindings(final Map<String, OperationBinding> bindingsMap, final String destination) {
        Assertions.assertTrue(bindingsMap.containsKey(destination));
    }

}
