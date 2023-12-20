package org.example.age.testing.json;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.data.json.JsonValues;

/** Utilities for testing JSON serialization. */
public final class JsonTester {

    /**
     * Serializes a value as JSON and then deserializes it,
     * verifying that the deserialized value {@code equals()} the original value.
     *
     * <p>Also returns the deserialized value.</p>
     */
    public static <V> V serializeThenDeserialize(V value, TypeReference<V> valueTypeRef) {
        byte[] rawValue = JsonValues.serialize(value);
        V rtValue = JsonValues.deserialize(rawValue, valueTypeRef);
        assertThat(rtValue).isEqualTo(value);
        return rtValue;
    }

    // static class
    private JsonTester() {}
}
