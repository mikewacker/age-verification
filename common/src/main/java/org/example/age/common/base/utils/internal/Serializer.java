package org.example.age.common.base.utils.internal;

/** Serializes a type into raw bytes. */
@FunctionalInterface
public interface Serializer<T> {

    byte[] serialize(T t);
}
