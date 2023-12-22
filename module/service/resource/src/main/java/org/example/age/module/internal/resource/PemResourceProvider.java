package org.example.age.module.internal.resource;

import java.nio.file.Path;
import java.util.function.Supplier;

/** Provides a key that is read from a PEM resource file. Is not refreshable. */
public abstract class PemResourceProvider<K> {

    private final ResourceLoader resourceLoader;
    private final Path path;

    @SuppressWarnings("this-escape")
    private final Supplier<K> keyProvider = DoubleCheckedProvider.create(this::loadKey);

    /** Gets the key. */
    public final K get() {
        return keyProvider.get();
    }

    /** Creates a key provider that reads a PEM resource file. */
    protected PemResourceProvider(ResourceLoader resourceLoader, Path path) {
        this.resourceLoader = resourceLoader;
        this.path = path;
    }

    /** Creates a key from a PEM object. */
    protected abstract K createKey(Object pemObject);

    /** Reads a PEM resource file and creates a key from it. */
    private K loadKey() {
        Object pemObject = resourceLoader.loadPem(path);
        return createKey(pemObject);
    }
}
