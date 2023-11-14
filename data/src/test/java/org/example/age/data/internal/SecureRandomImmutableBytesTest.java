package org.example.age.data.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.IOException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

public final class SecureRandomImmutableBytesTest {

    @Test
    public void generate() {
        TestObject o = TestObject.generate();
        assertThat(o.bytes()).hasSize(4);
        assertThat(o.bytes()).isNotEqualTo(new byte[4]);
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestObject o = TestObject.generate();
        byte[] rawO = mapper.writeValueAsBytes(o);
        TestObject rtO = mapper.readValue(rawO, new TypeReference<>() {});
        assertThat(rtO).isEqualTo(o);
    }

    @Test
    public void error_IllegalLength() {
        error_IllegalLength(() -> TestObject.ofBytes(new byte[1]));
        error_IllegalLength(() -> TestObject.fromString("AA"));
    }

    private void error_IllegalLength(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expected 32 bits");
    }

    /** Test {@link SecureRandomImmutableBytes}. */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = TestObject.Deserializer.class)
    private static final class TestObject extends SecureRandomImmutableBytes {

        private static final int EXPECTED_LENGTH = 4;

        public static TestObject ofBytes(byte[] rawO) {
            return new TestObject(rawO);
        }

        public static TestObject fromString(String rawO) {
            return new TestObject(rawO);
        }

        public static TestObject generate() {
            return new TestObject();
        }

        private TestObject(byte[] rawO) {
            super(rawO, EXPECTED_LENGTH);
        }

        private TestObject(String rawO) {
            super(rawO, EXPECTED_LENGTH);
        }

        private TestObject() {
            super(EXPECTED_LENGTH);
        }

        /** JSON {@code fromString()} deserializer. */
        static final class Deserializer extends StaticFromStringDeserializer<TestObject> {

            public Deserializer() {
                super(TestObject.class, TestObject::fromString);
            }
        }
    }
}
