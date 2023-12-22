package org.example.age.module.internal.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;

/** Loads resource files. */
public interface ResourceLoader {

    /** Loads and deserializes a value from a JSON file. */
    <V> V loadJson(Path path, TypeReference<V> valueTypeRef);

    /** Loads a PEM object from a PEM file. */
    Object loadPem(Path path);
}
