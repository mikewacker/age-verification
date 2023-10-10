package org.example.age.data.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.testing.EqualsTester;
import java.util.Arrays;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

public final class ImmutableBytesTest {

    private static final byte[] BYTES = new byte[] {
        -31, 98, -36, -70, -19, 26, 103, 100, -116, -60, 47, 69, 97, 84, 30, -128, -85, -5, -46, 46, 53, -24, 47, -28,
        -11, -11, -92, 98, 54, 33, 66, 80
    };
    private static final String BASE64_TEXT = "4WLcuu0aZ2SMxC9FYVQegKv70i416C_k9fWkYjYhQlA";

    @Test
    public void toString_() {
        TestObject o = TestObject.ofBytes(BYTES);
        assertThat(o.toString()).isEqualTo(BASE64_TEXT);
    }

    @Test
    public void fromString() {
        TestObject o = TestObject.fromString(BASE64_TEXT);
        assertThat(o.bytes()).isEqualTo(BYTES);
    }

    @Test
    public void generate() {
        TestObject o = TestObject.generate();
        assertThat(o.bytes()).hasSize(32);
        assertThat(o.bytes()).isNotEqualTo(new byte[32]);
    }

    @Test
    public void serializeThenDeserialize() {
        TestObject o = TestObject.ofBytes(BYTES);
        byte[] bytes = SerializationUtils.serialize(o);
        TestObject deserializedO = SerializationUtils.deserialize(bytes, TestObject.class);
        assertThat(deserializedO).isEqualTo(o);
    }

    @Test
    public void equals() {
        TestObject o1 = TestObject.fromString(BASE64_TEXT);
        TestObject o2 = TestObject.fromString(BASE64_TEXT);
        TestObject o3 = TestObject.ofBytes(new byte[32]);
        ImmutableBytes o4 = new ImmutableBytes(BASE64_TEXT) {};
        new EqualsTester()
                .addEqualityGroup(o1, o2)
                .addEqualityGroup(o3)
                .addEqualityGroup(o4)
                .testEquals();
    }

    @Test
    public void copyOnCreate() {
        byte[] bytes = Arrays.copyOf(BYTES, BYTES.length);
        TestObject o = TestObject.ofBytes(bytes);
        bytes[0] = 0;
        assertThat(bytes).isNotEqualTo(o.bytes());
    }

    @Test
    public void copyOnRead() {
        TestObject o = TestObject.ofBytes(BYTES);
        byte[] bytes = o.bytes();
        bytes[0] = 0;
        assertThat(bytes).isNotEqualTo(o.bytes());
    }

    @Test
    public void ofBytes_AnyLength() {
        new ImmutableBytes(new byte[4]) {};
        new ImmutableBytes(new byte[8]) {};
    }

    @Test
    public void error_IllegalLength_OfBytes() {
        error_IllegalLength(() -> TestObject.ofBytes(new byte[4]));
    }

    @Test
    public void error_IllegalLength_FromString() {
        error_IllegalLength(() -> TestObject.fromString("123456"));
    }

    private void error_IllegalLength(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expected 256 bits");
    }

    @Test
    public void error_Generate_WithoutLength() {
        assertThatThrownBy(() -> new ImmutableBytes() {})
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("expected length must be set");
    }

    /** Serializable test object that's backed by 256 bits. */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = TestObject.Deserializer.class)
    private static final class TestObject extends ImmutableBytes {

        public static TestObject ofBytes(byte[] bytes) {
            return new TestObject(bytes);
        }

        public static TestObject fromString(String value) {
            return new TestObject(value);
        }

        public static TestObject generate() {
            return new TestObject();
        }

        @Override
        protected int expectedLength() {
            return 32;
        }

        private TestObject(byte[] bytes) {
            super(bytes);
        }

        private TestObject(String value) {
            super(value);
        }

        private TestObject() {}

        /** JSON {@code fromString()} deserializer. */
        static final class Deserializer extends StaticFromStringDeserializer<TestObject> {

            public Deserializer() {
                super(TestObject.class, TestObject::fromString);
            }
        }
    }
}
