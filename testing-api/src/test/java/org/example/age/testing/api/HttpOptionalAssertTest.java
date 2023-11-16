package org.example.age.testing.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import org.assertj.core.api.ThrowableAssert;
import org.example.age.api.HttpOptional;
import org.junit.jupiter.api.Test;

public final class HttpOptionalAssertTest {

    @Test
    public void of() {
        HttpOptional<String> maybeValue = HttpOptional.of("a");
        assertThat(maybeValue).isPresent();
        assertThat(maybeValue).hasValue("a");
        assertionFailed(() -> assertThat(maybeValue).hasValue("b"));
        assertionFailed(() -> assertThat(maybeValue).isEmpty());
        assertionFailed(() -> assertThat(maybeValue).isEmptyWithStatusCode(500));
    }

    @Test
    public void empty() {
        HttpOptional<String> maybeValue = HttpOptional.empty(500);
        assertionFailed(() -> assertThat(maybeValue).isPresent());
        assertionFailed(() -> assertThat(maybeValue).hasValue("a"));
        assertThat(maybeValue).isEmpty();
        assertThat(maybeValue).isEmptyWithStatusCode(500);
        assertionFailed(() -> assertThat(maybeValue).isEmptyWithStatusCode(400));
    }

    private void assertionFailed(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable).isInstanceOf(AssertionError.class);
    }
}
