package org.example.age.common.base.utils.internal;

/** Deserializes text into a type. */
@FunctionalInterface
public interface TextDeserializer<T> {

    T deserialize(String text) throws Exception;
}
