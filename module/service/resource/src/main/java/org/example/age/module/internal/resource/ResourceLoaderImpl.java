package org.example.age.module.internal.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.data.json.JsonValues;

@Singleton
final class ResourceLoaderImpl implements ResourceLoader {

    private final ClassLoader classLoader;

    @Inject
    public ResourceLoaderImpl(@Named("resources") Class<?> resourcesClass) {
        classLoader = resourcesClass.getClassLoader();
    }

    @Override
    public <V> V loadJson(Path path, TypeReference<V> valueTypeRef) {
        byte[] rawValue = loadBytes(path);
        return JsonValues.deserialize(rawValue, valueTypeRef);
    }

    /** Loads a resource file as raw bytes. */
    private byte[] loadBytes(Path path) {
        try (InputStream stream = loadInputStream(path)) {
            return stream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Loads an input stream for a resource file. */
    private InputStream loadInputStream(Path path) {
        InputStream stream = classLoader.getResourceAsStream(path.toString());
        if (stream == null) {
            String message = String.format("resource not found: %s", path);
            throw new IllegalArgumentException(message);
        }

        return stream;
    }
}
