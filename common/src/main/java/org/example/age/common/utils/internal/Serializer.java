package org.example.age.common.utils.internal;

/** Serializes a type into raw bytes. */
@FunctionalInterface
public interface Serializer<T> {

    byte[] serialize(T t);
}
