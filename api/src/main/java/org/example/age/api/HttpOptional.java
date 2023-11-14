package org.example.age.api;

import java.util.NoSuchElementException;
import java.util.Objects;

/** A non-null value and a 200 status code, or an error status code if the value is empty. */
public final class HttpOptional<V> {

    private final V value;
    private final int statusCode;

    /** Creates a {@link HttpOptional} with the provided value and a 200 status code. */
    public static <V> HttpOptional<V> of(V value) {
        Objects.requireNonNull(value);
        return new HttpOptional<>(value, 200);
    }

    /** Creates an empty {@link HttpOptional} with the provided status code. */
    public static <V> HttpOptional<V> empty(int statusCode) {
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
    public V get() {
        if (value == null) {
            throw new NoSuchElementException();
        }

        return value;
    }

    /** Gets the status code. */
    public int statusCode() {
        return statusCode;
    }

    @Override
    public boolean equals(Object o) {
        HttpOptional<?> other = (o instanceof HttpOptional) ? (HttpOptional<?>) o : null;
        if (other == null) {
            return false;
        }

        return Objects.equals(value, other.value) && (statusCode == other.statusCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, statusCode);
    }

    @Override
    public String toString() {
        return (value != null)
                ? String.format("HttpOptional[%s]", value)
                : String.format("HttpOptional.empty[%d]", statusCode);
    }

    private HttpOptional(V result, int code) {
        this.value = result;
        this.statusCode = code;
    }
}