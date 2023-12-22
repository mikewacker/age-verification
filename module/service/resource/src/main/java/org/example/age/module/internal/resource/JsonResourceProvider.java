package org.example.age.module.internal.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;

/** Provides a value that is read and deserialized from a JSON resource file. Is not refreshable. */
public abstract class JsonResourceProvider<V> {

    private final V value;

    /** Creates a value provider that reads and deserializes a JSON resource file. */
    protected JsonResourceProvider(ResourceLoader resourceLoader, Path path, TypeReference<V> valueTypeRef) {
        value = resourceLoader.loadJson(path, valueTypeRef);
    }

    /** Gets the value. */
    protected final V getInternal() {
        return value;
    }
}
