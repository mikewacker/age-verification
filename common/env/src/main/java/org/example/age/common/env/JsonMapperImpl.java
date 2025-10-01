package org.example.age.common.env;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/** Implementation of {@link JsonMapper}. */
@Singleton
final class JsonMapperImpl implements JsonMapper {

    private final ObjectMapper mapper;

    @Inject
    public JsonMapperImpl(LiteEnv env) {
        this.mapper = env.jsonMapper();
    }

    @Override
    public String serialize(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON serialization failed", e);
        }
    }

    @Override
    public <V> V deserialize(String json, Class<V> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON deserialization failed", e);
        }
    }
}
