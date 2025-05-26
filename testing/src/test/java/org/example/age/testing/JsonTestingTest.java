package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.IOException;
import org.example.age.api.AgeCertificate;
import org.junit.jupiter.api.Test;

public final class JsonTestingTest {

    @Test
    public void serializeThenDeserialize_AgeCertificate() throws IOException {
        JsonTesting.serializeThenDeserialize(TestModels.createAgeCertificate(), AgeCertificate.class);
    }

    @Test
    public void serializeThenDeserialize_BadValue() {
        assertThatThrownBy(() -> JsonTesting.serializeThenDeserialize(BadValue.fromString("value"), BadValue.class))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void serializeThenDeserialize_TestUtils() {
        String json = JsonTesting.serialize(TestModels.createAgeCertificate());
        AgeCertificate ageCertificate = JsonTesting.deserialize(json, AgeCertificate.class);
        assertThat(ageCertificate).isNotNull();
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
