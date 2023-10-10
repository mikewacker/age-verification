package org.example.age.data.internal;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import java.util.function.Function;

/**
 * Deserializes a class using a static {@code fromString()} factory method.
 *
 * <p>Concrete implementations only need to provide a public no-arg constructor.</p>
 */
public abstract class StaticFromStringDeserializer<T> extends FromStringDeserializer<T> {

    private final Function<String, T> fromString;

    protected StaticFromStringDeserializer(Class<T> clazz, Function<String, T> fromString) {
        super(clazz);
        this.fromString = fromString;
    }

    @Override
    protected T _deserialize(String value, DeserializationContext context) {
        return fromString.apply(value);
    }
}
