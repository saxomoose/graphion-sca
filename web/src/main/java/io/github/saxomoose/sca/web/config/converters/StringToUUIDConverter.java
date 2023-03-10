package io.github.saxomoose.sca.web.config.converters;

import jakarta.validation.constraints.NotBlank;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StringToUUIDConverter implements Converter<String, UUID> {

    @Override
    public UUID convert(@NotBlank String source) {
        return UUID.fromString(source) ;
    }
}
