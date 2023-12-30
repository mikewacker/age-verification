package org.example.age.testing.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

public final class JsonTesterTest {

    @Test
    public void serializeThenDeserialize_Pass() {
        String text = "test";
        String rtText = JsonTester.serializeThenDeserialize(text, new TypeReference<>() {});
        assertThat(rtText).isNotSameAs(text);
    }

    @Test
    public void serializeThenDeserialize_Fail() {
        assertThatThrownBy(() -> JsonTester.serializeThenDeserialize(new BadValue("test"), new TypeReference<>() {}))
                .isInstanceOf(AssertionError.class);
    }

    /** Test value that deserializes incorrectly. */
    private record BadValue(String text) {

        @JsonCreator
        @SuppressWarnings("unused")
        public static BadValue deserialize(String text) {
            return new BadValue("abc123");
        }

        @JsonValue
        public String serialize() {
            return text;
        }
    }
}
