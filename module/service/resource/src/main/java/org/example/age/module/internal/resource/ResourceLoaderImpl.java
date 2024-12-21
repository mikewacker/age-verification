package org.example.age.module.internal.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.json.JsonValues;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.bouncycastle.openssl.PEMParser;

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

    @Override
    public Object loadPem(Path path) {
        try (InputStream in = loadInputStream(path);
                Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                PEMParser pemParser = new PEMParser(reader)) {
            return pemParser.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Loads a resource file as raw bytes. */
    private byte[] loadBytes(Path path) {
        try (InputStream in = loadInputStream(path)) {
            return in.readAllBytes();
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
