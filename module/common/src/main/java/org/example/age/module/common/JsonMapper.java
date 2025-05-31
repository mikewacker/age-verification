package org.example.age.module.common;

/** JSON serialization. Wraps checked exceptions. */
public interface JsonMapper {

    /** Serializes a value to JSON. */
    String serialize(Object value);

    /** Deserializes a value from JSON. */
    <V> V deserialize(String json, Class<V> valueType);
}
