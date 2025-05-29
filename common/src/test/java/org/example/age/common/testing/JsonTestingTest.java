package org.example.age.common.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public final class JsonTestingTest {

    @Test
    public void serializeThenDeserialize_String() throws IOException {
        JsonTesting.serializeThenDeserialize("test", String.class);
    }

    @Test
    public void serializeThenDeserialize_BadValue() {
        assertThatThrownBy(() -> JsonTesting.serializeThenDeserialize(BadValue.fromString("value"), BadValue.class))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void serializeThenDeserialize_TestUtils() {
        String json = JsonTesting.serialize("test");
        String value = JsonTesting.deserialize(json, String.class);
        assertThat(value).isEqualTo("test");
    }

    /** Value type that serializes incorrectly. */
    record BadValue(String value) {

        @JsonCreator
        public static BadValue fromString(String value) {
            return new BadValue(value);
        }

        @JsonValue
        @Override
        public String toString() {
            return "error";
        }
    }
}
