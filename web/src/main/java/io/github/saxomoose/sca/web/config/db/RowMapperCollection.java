package io.github.saxomoose.sca.web.config.db;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

public class RowMapperCollection {
    private final Map<Class<?>, RowMapper<?>> mappers = new LinkedHashMap<>();
    
    @SuppressWarnings("unchecked")
    public <T> RowMapper<? extends T> getRowMapper(final Class<T> type) 
    {
        Assert.notNull(type, "Type must not be null");
        RowMapper<?> candidate = mappers.get(type);
        if (candidate == null) {
            for (final Map.Entry<Class<?>, RowMapper<?>> entry : mappers.entrySet()) {
                if (type.isAssignableFrom(entry.getKey())) {
                    candidate = entry.getValue();
                }
            }
        }
        return (RowMapper<? extends T>) candidate;
    }

    /**
     * Registers a the given {@link RowMapper} as to be used for the given type.
     *
     * @return this instance, so this can be used as a fluent interface.
     */
    public <T> RowMapperCollection registerRowMapper(final Class<T> type, final RowMapper<? extends T> rowMapper) 
    {
        mappers.put(type, rowMapper);
        return this;
    }
}
