package org.example.age.module.internal.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;
import java.util.function.Supplier;

/** Provides a value that is read and deserialized from a JSON resource file. Is not refreshable. */
public abstract class JsonResourceProvider<V> {

    private final ResourceLoader resourceLoader;
    private final Path path;
    private final TypeReference<V> valueTypeRef;

    private final Supplier<V> valueProvider = DoubleCheckedProvider.create(this::loadValue);

    /** Creates a provider that reads and deserializes a JSON resource file. */
    protected JsonResourceProvider(ResourceLoader resourceLoader, Path path, TypeReference<V> valueTypeRef) {
        this.resourceLoader = resourceLoader;
        this.path = path;
        this.valueTypeRef = valueTypeRef;
    }

    /** Gets the value. */
    protected final V getInternal() {
        return valueProvider.get();
    }

    /** Reads and deserializes a JSON resource file. */
    private V loadValue() {
        return resourceLoader.loadJson(path, valueTypeRef);
    }
}
