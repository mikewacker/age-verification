package org.example.age.common.api.exchange.impl;

/** Serializes a type into raw bytes. */
@FunctionalInterface
public interface BytesSerializer<T> {

    byte[] serialize(T t) throws Exception;
}
