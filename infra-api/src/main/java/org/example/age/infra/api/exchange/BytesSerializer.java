package org.example.age.infra.api.exchange;

/** Serializes a type into raw bytes. */
@FunctionalInterface
public interface BytesSerializer<T> {

    byte[] serialize(T t) throws Exception;
}
