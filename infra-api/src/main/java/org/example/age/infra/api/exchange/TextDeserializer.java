package org.example.age.infra.api.exchange;

/** Deserializes text into a type. */
@FunctionalInterface
public interface TextDeserializer<T> {

    T deserialize(String text) throws Exception;
}
