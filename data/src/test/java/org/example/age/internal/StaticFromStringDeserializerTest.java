package org.example.age.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.junit.jupiter.api.Test;

public final class StaticFromStringDeserializerTest {

    @Test
    public void serializeThenDeserialize() {
        TestObject o = TestObject.fromString("test");
        byte[] bytes = SerializationUtils.serialize(o);
        TestObject deserializedO = SerializationUtils.deserialize(bytes, TestObject.class);
        assertThat(deserializedO.toString()).isEqualTo(o.toString());
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
