package org.example.age.common.api.exchange.impl;

/** Deserializes raw bytes into a type. */
@FunctionalInterface
public interface BytesDeserializer<T> {

    T deserialize(byte[] bytes) throws Exception;
}
