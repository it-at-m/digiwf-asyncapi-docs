package io.muenchendigital.digiwf;

import com.asyncapi.v2.binding.OperationBinding;
import com.asyncapi.v2.binding.kafka.KafkaOperationBinding;
import com.asyncapi.v2.model.channel.ChannelItem;
import com.asyncapi.v2.model.channel.operation.Operation;
import io.github.stavshamir.springwolf.asyncapi.scanners.channels.ChannelsScanner;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.message.Message;
import io.github.stavshamir.springwolf.asyncapi.types.channel.operation.message.PayloadReference;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.*;

@Slf4j
@AllArgsConstructor
public class SpringCloudStreamChannelScanner implements ChannelsScanner {

    private final SchemasService schemasService;
    private List<String> definitions;
    private Map<String, Map<String, String>> bindings;
    private String basePackage = "";

    @Override
    public Map<String, ChannelItem> scan() {
        final Map<String, ChannelItem> channels = new HashMap<>();

        // get @DocumentAsyncAPI classes
        final Reflections reflections = new Reflections(this.basePackage);
        final Set<Class<?>> annotatedConsumersAndProducers = reflections.getTypesAnnotatedWith(DocumentAsyncAPI.class);

        // add each binding (consumers and producers) to documentation
        this.bindings.keySet().forEach(binding -> {
            final Map<String, String> bindingProps = this.bindings.get(binding);

            // verify that cloud function is declared for binding
            final Optional<String> definition = this.definitions.stream()
                    .filter(def -> def.equals(binding.split("-")[0]))
                    .findAny();

            if (definition.isEmpty()) {
                log.warn("Cloud function does not exist for binding {}", binding);
                return;
            }

            // check that cloud function exists in class with @DocumentAsyncAPI annotation
            final var annotatedCloudFunction = annotatedConsumersAndProducers
                    .stream()
                    .filter(annotated -> Arrays.stream(annotated.getDeclaredMethods())
                            .anyMatch(method -> definition.get().equals(method.getName())))
                    .findAny();

            if (annotatedCloudFunction.isEmpty()) {
                log.warn("No documentation found for {}. Did you annotate your cloud function with @DocumentAsyncAPI", definition.get());
                return;
            }

            // if destination is not specified ignore the cloud function
            // if you want to use a function router you have to specify the producers manually
            if (bindingProps.get("destination") == null) {
                log.warn("No destination specified for {}", definition.get());
                return;
            }

            final KafkaOperationBinding kafkaBinding = new KafkaOperationBinding();

            final String group = bindingProps.get("group");
            if (group != null) {
                kafkaBinding.setGroupId(group);
            }

            final String destination = bindingProps.get("destination");
            final Class<?> payload = annotatedCloudFunction.get().getAnnotation(DocumentAsyncAPI.class).payload();
            final Operation operation = this.createOperation(payload, List.of(bindingProps.get("destination").split(",")), kafkaBinding);

            // finally put channel items together for consumers (in) and producers (out)
            if (binding.contains("in")) {
                // ChannelItem
                final ChannelItem channelItem = ChannelItem.builder()
                        .publish(operation)
                        .build();
                channels.put(destination, channelItem);
            }
            if (binding.contains("out")) {
                // ChannelItem
                final ChannelItem channelItem = ChannelItem.builder()
                        .subscribe(operation)
                        .build();
                channels.put(destination, channelItem);
            }
        });

        // input function router specific
        if (this.definitions.stream()
                .anyMatch(def -> def.equals("functionRouter"))) {
            final Map<String, String> bindingProps = this.bindings.get("functionRouter-in-0");
            final KafkaOperationBinding kafkaBinding = new KafkaOperationBinding();

            final String group = bindingProps.get("group");
            if (group != null) {
                kafkaBinding.setGroupId(group);
            }

            final Operation operation = this.createOperation(Object.class, List.of(bindingProps.get("destination").split(",")), kafkaBinding);
            // ChannelItem
            final ChannelItem channelItem = ChannelItem.builder()
                    .publish(operation)
                    .build();
            channels.put(bindingProps.get("destination"), channelItem);
        }

        return channels;
    }

    private Operation createOperation(final Class<?> payload, final List<String> destination, final KafkaOperationBinding kafkaOperationBinding) {
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
