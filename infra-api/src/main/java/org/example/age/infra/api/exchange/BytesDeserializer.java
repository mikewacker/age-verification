package org.example.age.infra.api.exchange;

/** Deserializes raw bytes into a type. */
@FunctionalInterface
public interface BytesDeserializer<T> {

    T deserialize(byte[] bytes) throws Exception;
}
