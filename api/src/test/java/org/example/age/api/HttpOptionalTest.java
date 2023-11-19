package org.example.age.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.testing.EqualsTester;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public final class HttpOptionalTest {

    @Test
    public void of() {
        HttpOptional<String> maybeValue = HttpOptional.of("a");
        assertThat(maybeValue.isEmpty()).isFalse();
        assertThat(maybeValue.isPresent()).isTrue();
        assertThat(maybeValue.get()).isEqualTo("a");
        assertThat(maybeValue.statusCode()).isEqualTo(200);
        assertThat(maybeValue.toString()).isEqualTo("HttpOptional[a]");
    }

    @Test
    public void empty() {
        HttpOptional<String> maybeValue = HttpOptional.empty(500);
        assertThat(maybeValue.isEmpty()).isTrue();
        assertThat(maybeValue.isPresent()).isFalse();
        assertThat(maybeValue.statusCode()).isEqualTo(500);
        assertThat(maybeValue.toString()).isEqualTo("HttpOptional.empty[500]");
    }

    @Test
    public void fromOptional_Of() {
        HttpOptional<String> maybeValue = HttpOptional.fromOptional(Optional.of("a"), 500);
        assertThat(maybeValue.isEmpty()).isFalse();
        assertThat(maybeValue.get()).isEqualTo("a");
    }

    @Test
    public void fromOptional_Empty() {
        HttpOptional<String> maybeValue = HttpOptional.fromOptional(Optional.empty(), 500);
        assertThat(maybeValue.isEmpty()).isTrue();
        assertThat(maybeValue.statusCode()).isEqualTo(500);
    }

    @Test
    public void equals() {
        new EqualsTester()
                .addEqualityGroup(HttpOptional.of("a"), HttpOptional.of("a"))
                .addEqualityGroup(HttpOptional.of("b"))
                .addEqualityGroup(HttpOptional.of(1))
                .addEqualityGroup(HttpOptional.empty(400), HttpOptional.empty(400))
                .addEqualityGroup(HttpOptional.empty(500))
                .testEquals();
    }

    @Test
    public void error_Of_Null() {
        assertThatThrownBy(() -> HttpOptional.of(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void error_Get_Empty() {
        HttpOptional<String> maybeValue = HttpOptional.empty(500);
        assertThatThrownBy(maybeValue::get).isInstanceOf(NoSuchElementException.class);
    }
}
