package org.example.age.testing.api;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.CheckReturnValue;
import org.example.age.api.HttpOptional;

/** Assertions for {@link HttpOptional}. */
@CheckReturnValue
public final class HttpOptionalAssert<V> {

    private final HttpOptional<V> maybeValue;

    /** Entry point for assertions. */
    public static <V> HttpOptionalAssert<V> assertThat(HttpOptional<V> maybeValue) {
        return new HttpOptionalAssert<>(maybeValue);
    }

    /** Verifies that a value is present. */
    public void isPresent() {
        Assertions.assertThat(maybeValue.isPresent()).isTrue();
    }

    /** Verifies that the expected value is present. */
    public void hasValue(V expectedValue) {
        isPresent();
        Assertions.assertThat(maybeValue.get()).isEqualTo(expectedValue);
    }

    /** Verifies that a value is not present. */
    public void isEmpty() {
        Assertions.assertThat(maybeValue.isEmpty()).isTrue();
    }

    /** Verifies that a value is not present and that the status code is the expected error status code. */
    public void isEmptyWithErrorCode(int expectedErrorCode) {
        isEmpty();
        Assertions.assertThat(maybeValue.statusCode()).isEqualTo(expectedErrorCode);
    }

    private HttpOptionalAssert(HttpOptional<V> maybeValue) {
        this.maybeValue = maybeValue;
    }
}
