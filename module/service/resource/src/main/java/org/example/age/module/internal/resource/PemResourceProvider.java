package org.example.age.module.internal.resource;

import java.nio.file.Path;

/** Provides a key that is read from a PEM resource file. Is not refreshable. */
public abstract class PemResourceProvider<K> {

    private final K key;

    /** Creates a key provider that reads a PEM resource file. */
    @SuppressWarnings("this-escape")
    protected PemResourceProvider(ResourceLoader resourceLoader, Path path) {
        Object pemObject = resourceLoader.loadPem(path);
        key = createKey(pemObject);
    }

    /** Gets the key. */
    protected final K getInternal() {
        return key;
    }

    /** Creates a key from a PEM object. */
    protected abstract K createKey(Object pemObject);
}
