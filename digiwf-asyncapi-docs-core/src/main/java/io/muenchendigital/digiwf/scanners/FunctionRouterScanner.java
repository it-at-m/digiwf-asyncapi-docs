package io.muenchendigital.digiwf.scanners;

import com.asyncapi.v2.binding.kafka.KafkaOperationBinding;
import com.asyncapi.v2.model.channel.ChannelItem;
import com.asyncapi.v2.model.channel.operation.Operation;
import io.github.stavshamir.springwolf.asyncapi.scanners.channels.ChannelsScanner;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import io.muenchendigital.digiwf.annotations.DocumentAsyncAPI;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.MethodsAnnotated;

/**
 * FunctionRouter scanner generates ChannelItems if the spring cloud stream function router is enabled
 *
 * Annotate your cloud functions with @DocumentAsyncAPI(payload = YourClass.class, functionRouter = true, typeHeader = "yourTypeHeader") for this scanner to detect them.
 */
@Slf4j
public class FunctionRouterScanner extends SpringCloudStreamScanner implements ChannelsScanner {

    public FunctionRouterScanner(SchemasService schemasService, List<String> definitions, Map<String, Map<String, String>> bindings, String basePackage) {
        super(schemasService, definitions, bindings, basePackage);
    }

    /**
     * Generates ChannelItems for spring cloud stream consumers based on configuration properties and @DocumentAsyncAPI annotation.
     * The channelItems are added to the springwolf documentation.
     *
     * @return channelItems
     */
    @Override
    public Map<String, ChannelItem> scan() {
        final Map<String, ChannelItem> channels = new HashMap<>();

        // get classes annotated with @DocumentAsyncAPI and function router enabled
        final Reflections reflections = new Reflections(this.basePackage, Scanners.values());
        final Set<Method> functionRouterConsumers = reflections.get(MethodsAnnotated.with(DocumentAsyncAPI.class).as(Method.class))
                .stream()
                .filter(method -> method.getAnnotation(DocumentAsyncAPI.class).functionRouter())
                .collect(Collectors.toSet());

        final String binding = "functionRouter-in-0";
        final Optional<String> definition = this.definitions.stream()
                .filter(def -> def.equals(binding.split("-")[0]))
                .findAny();

        if (definition.isEmpty()) {
            log.warn("Cloud function does not exist for binding {}", binding);
        } else {
            final List<Class<?>> payloads = this.getPayload(functionRouterConsumers).orElse(new ArrayList<>());
            payloads.forEach(payload -> {
                // get group, destination and payload
                final String group = this.bindings.get(binding).get("group");
                final String destination = this.bindings.get(binding).get("destination");

                // put it all together to a ChannelItem
                final KafkaOperationBinding kafkaBinding = new KafkaOperationBinding();
                kafkaBinding.setGroupId(group);
                final Operation operation = this.createOperation(payload, List.of(destination.split(",")), kafkaBinding);
                final ChannelItem channelItem = ChannelItem.builder().build();

                channelItem.setPublish(operation);

                final Optional<Method> consumer = functionRouterConsumers.stream()
                        .filter(c -> {
                            final DocumentAsyncAPI annotation = c.getAnnotation(DocumentAsyncAPI.class);
                            return !annotation.typeHeader().isBlank() && annotation.payload().equals(payload);
                        }).findAny();

                // add the created channelItem to channels if type header is present
                consumer.ifPresent(method -> channels.put(method.getAnnotation(DocumentAsyncAPI.class).typeHeader() + ": " + destination, channelItem));
            });
        }

        return channels;
    }

}
