package org.example.age.common.api.exchange.impl;

/** Deserializes text into a type. */
@FunctionalInterface
public interface TextDeserializer<T> {

    T deserialize(String text) throws Exception;
}
