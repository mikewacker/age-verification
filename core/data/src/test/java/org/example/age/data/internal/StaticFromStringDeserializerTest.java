package org.example.age.data.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.example.age.api.JsonSerializer;
import org.junit.jupiter.api.Test;

public final class StaticFromStringDeserializerTest {

    @Test
    public void serializeThenDeserialize() {
        TestObject o = TestObject.fromString("test");
        byte[] rawO = JsonSerializer.serialize(o);
        TestObject rtO = JsonSerializer.deserialize(rawO, new TypeReference<>() {});
        assertThat(rtO.toString()).isEqualTo(o.toString());
    }

    /** Serializable test object. */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = TestObject.Deserializer.class)
    private static final class TestObject {

        private final String text;

        public static TestObject fromString(String text) {
            return new TestObject(text);
        }

        @Override
        public String toString() {
            return text;
        }

        private TestObject(String text) {
            this.text = text;
        }

        /** JSON {@code fromString()} deserializer. */
        static final class Deserializer extends StaticFromStringDeserializer<TestObject> {

            public Deserializer() {
                super(TestObject.class, TestObject::fromString);
            }
        }
    }
}
