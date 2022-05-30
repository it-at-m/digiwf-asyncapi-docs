package io.muenchendigital.digiwf.scanners;

import com.asyncapi.v2.binding.OperationBinding;
import com.asyncapi.v2.binding.kafka.KafkaOperationBinding;
import com.asyncapi.v2.model.channel.ChannelItem;
import com.asyncapi.v2.model.channel.operation.Operation;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.message.Message;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.message.PayloadReference;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import io.muenchendigital.digiwf.annotations.DocumentAsyncAPI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

/**
 * ConsumerAndProducerScanner generates ChannelItems for spring cloud stream consumers and producers based on
 * configuration properties.
 */
@Slf4j
@AllArgsConstructor
public abstract class SpringCloudStreamScanner {

    final SchemasService schemasService;
    List<String> definitions;
    Map<String, Map<String, String>> bindings;
    String basePackage = "";

    /**
     * Generates ChannelItems for spring cloud stream consumers and producers based on configuration properties and @DocumentAsyncAPI annotation.
     * The channelItems are added to the springwolf documentation.
     *
     * @return channelItems
     */
    public Map<String, ChannelItem> scan(final Set<Method> annotatedConsumersAndProducers) {
        final Map<String, ChannelItem> channels = new HashMap<>();

        this.bindings.keySet().forEach(binding -> {
            final Optional<String> definition = this.definitions.stream()
                    .filter(def -> def.equals(binding.split("-")[0]))
                    .findAny();

            if (definition.isEmpty()) {
                log.warn("Cloud function does not exist for binding {}", binding);
                return;
            }

            // get group, destination and payload
            final String group = this.bindings.get(binding).get("group");
            final String destination = this.bindings.get(binding).get("destination");
            final Class<?> payload = this.getPayload(annotatedConsumersAndProducers, definition.get()).orElse(Object.class);

            // put it all together to a ChannelItem
            final KafkaOperationBinding kafkaBinding = new KafkaOperationBinding();
            kafkaBinding.setGroupId(group);
            final Operation operation = this.createOperation(payload, List.of(destination.split(",")), kafkaBinding);
            final ChannelItem channelItem = ChannelItem.builder().build();

            // if destination is not specified ignore the cloud function
            // if you want to use a function router you have to specify the producers manually
            if (destination.isBlank()) {
                log.warn("No destination specified for {}", definition.get());
                return;
            }

            if (binding.contains("in")) {
                channelItem.setPublish(operation);
            }
            if (binding.contains("out")) {
                channelItem.setSubscribe(operation);
            }

            // add the created channelItem to channels
            channels.put(destination, channelItem);
        });
        return channels;
    }

    /**
     * Get the payload class type from consumer and producers classes annotated with @DocumentAsyncAPI annotation.
     *
     * @param annotatedConsumersAndProducers
     * @param definition
     * @return
     */
    Optional<Class<?>> getPayload(final Set<Method> annotatedConsumersAndProducers, final String definition) {
        final var annotatedCloudFunction = annotatedConsumersAndProducers
                .stream()
                .filter(annotated -> definition.equals(annotated.getName()))
                .findAny();
        if (annotatedCloudFunction.isEmpty()) {
            log.warn("No documentation found for {}. Did you annotate your cloud function with @DocumentAsyncAPI", definition);
            return Optional.empty();
        }
        return Optional.of(annotatedCloudFunction.get().getAnnotation(DocumentAsyncAPI.class).payload());
    }

    Optional<List<Class<?>>> getPayload(final Set<Method> annotatedConsumersAndProducers) {
        List<Class<?>> payloads = new ArrayList<>();
        annotatedConsumersAndProducers.forEach(method -> payloads.add(method.getAnnotation(DocumentAsyncAPI.class).payload()));

        if (payloads.isEmpty()) {
            log.warn("No documentation found for {}. Did you annotate your cloud function with @DocumentAsyncAPI");
            return Optional.empty();
        }
        return Optional.of(payloads);
    }

    /**
     * Create Operation item
     *
     * @param payload
     * @param destination
     * @param kafkaOperationBinding
     * @return
     */
    Operation createOperation(final Class<?> payload, final List<String> destination, final KafkaOperationBinding kafkaOperationBinding) {
        final String modelName = this.schemasService.register(payload);
        final Message msg = Message.builder()
                .name(payload.getName())
                .title(modelName)
                .payload(PayloadReference.fromModelName(modelName))
                .build();

        final Map<String, OperationBinding> bindings = new HashMap<>();
        destination.forEach(dest -> {
            bindings.put(dest, kafkaOperationBinding);
        });

        return Operation.builder()
                .message(msg)
                .bindings(bindings)
                .build();
    }

}
