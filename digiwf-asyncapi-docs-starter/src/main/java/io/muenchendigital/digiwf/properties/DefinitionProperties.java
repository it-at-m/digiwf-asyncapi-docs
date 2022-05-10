package io.muenchendigital.digiwf.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
public class DefinitionProperties {

    @Value("#{'${spring.cloud.function.definition}'.split(';')}")
    private List<String> definitions;

}
