package org.example.age.common.base.utils.internal;

/** Serializes a type into raw bytes. */
@FunctionalInterface
public interface BytesSerializer<T> {

    byte[] serialize(T t) throws Exception;
}
