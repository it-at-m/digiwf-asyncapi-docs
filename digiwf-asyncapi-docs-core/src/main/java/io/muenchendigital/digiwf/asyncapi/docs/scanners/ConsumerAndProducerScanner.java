package io.muenchendigital.digiwf.asyncapi.docs.scanners;

import com.asyncapi.v2.model.channel.ChannelItem;
import io.github.stavshamir.springwolf.asyncapi.scanners.channels.ChannelsScanner;
import io.github.stavshamir.springwolf.schemas.SchemasService;
import io.muenchendigital.digiwf.asyncapi.docs.annotations.DocumentAsyncAPI;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.reflections.scanners.Scanners.MethodsAnnotated;

/**
 * ConsumerAndProducerScanner generates ChannelItems for spring cloud stream consumers and producers based on
 * configuration properties.
 */
@Slf4j
public class ConsumerAndProducerScanner extends SpringCloudStreamScanner implements ChannelsScanner {

    public ConsumerAndProducerScanner(final SchemasService schemasService, final List<String> definitions, final Map<String, Map<String, String>> bindings, final String basePackage) {
        super(schemasService, definitions, bindings, basePackage);
    }

    /**
     * Generates ChannelItems for spring cloud stream consumers and producers based on configuration properties and @DocumentAsyncAPI annotation.
     * The channelItems are added to the springwolf documentation.
     *
     * @return channelItems
     */
    @Override
    public Map<String, ChannelItem> scan() {
        // get classes annotated with @DocumentAsyncAPI
        final Reflections reflections = new Reflections(this.basePackage, Scanners.values());
        final Set<Method> annotatedConsumersAndProducers = reflections.get(MethodsAnnotated.with(DocumentAsyncAPI.class).as(Method.class));
        return super.scan(annotatedConsumersAndProducers);
    }

}
