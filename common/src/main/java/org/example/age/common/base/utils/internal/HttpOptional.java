package org.example.age.common.base.utils.internal;

import io.undertow.util.StatusCodes;
import java.util.NoSuchElementException;
import java.util.Objects;

/** A non-null value and a 200 status code, or an error status code if the value is empty. */
public final class HttpOptional<T> {

    private final T value;
    private final int statusCode;

    /** Creates a {@link HttpOptional} with the provided value and a 200 status code. */
    public static <T> HttpOptional<T> of(T value) {
        Objects.requireNonNull(value);
        return new HttpOptional<>(value, StatusCodes.OK);
    }

    /** Creates an empty {@link HttpOptional} with the provided status code. */
    public static <T> HttpOptional<T> empty(int statusCode) {
        return new HttpOptional<>(null, statusCode);
    }

    /** Determines if a value is empty. */
    public boolean isEmpty() {
        return value == null;
    }

    /** Determines if a value is present. */
    public boolean isPresent() {
        return value != null;
    }

    /** Gets the value. */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException();
        }

        return value;
    }

    /** Gets the status code. */
    public int statusCode() {
        return statusCode;
    }

    private HttpOptional(T result, int code) {
        this.value = result;
        this.statusCode = code;
    }
}
