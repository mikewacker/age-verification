package org.example.age.common.utils.internal;

/** Deserializes raw bytes into a type. */
@FunctionalInterface
public interface Deserializer<T> {

    T deserialize(byte[] bytes) throws Exception;
}
