package org.example.age.data.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.testing.EqualsTester;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public final class ImmutableBytesTest {

    private static final byte[] RAW_O_BYTES = new byte[] {-4, 121, 24, 46, -125, 48, -125, -112};
    private static final String RAW_O_TEXT = "_HkYLoMwg5A";

    @Test
    public void toString_() {
        TestObject o = TestObject.ofBytes(RAW_O_BYTES);
        assertThat(o.toString()).isEqualTo(RAW_O_TEXT);
    }

    @Test
    public void fromString() {
        TestObject o = TestObject.fromString(RAW_O_TEXT);
        assertThat(o.bytes()).isEqualTo(RAW_O_BYTES);
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestObject o = TestObject.ofBytes(RAW_O_BYTES);
        byte[] rawO = mapper.writeValueAsBytes(o);
        TestObject rtO = mapper.readValue(rawO, new TypeReference<>() {});
        assertThat(rtO).isEqualTo(o);
    }

    @Test
    public void equals() {
        new EqualsTester()
                .addEqualityGroup(TestObject.ofBytes(RAW_O_BYTES), TestObject.ofBytes(RAW_O_BYTES))
                .addEqualityGroup(TestObject.ofBytes(new byte[4]))
                .addEqualityGroup(new ImmutableBytes(RAW_O_BYTES) {})
                .testEquals();
    }

    @Test
    public void copyOnCreate() {
        byte[] rawO = Arrays.copyOf(RAW_O_BYTES, RAW_O_BYTES.length);
        TestObject o = TestObject.ofBytes(rawO);
        rawO[0] = 0;
        assertThat(rawO).isNotEqualTo(o.bytes());
    }

    @Test
    public void copyOnRead() {
        TestObject o = TestObject.ofBytes(RAW_O_BYTES);
        byte[] rawO = o.bytes();
        rawO[0] = 0;
        assertThat(rawO).isNotEqualTo(o.bytes());
    }

    /** Test {@link ImmutableBytes}. */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = TestObject.Deserializer.class)
    private static final class TestObject extends ImmutableBytes {

        public static TestObject ofBytes(byte[] rawO) {
            return new TestObject(rawO);
        }

        public static TestObject fromString(String rawO) {
            return new TestObject(rawO);
        }

        private TestObject(byte[] rawO) {
            super(rawO, true);
        }

        private TestObject(String rawO) {
            super(rawO);
        }

        /** JSON {@code fromString()} deserializer. */
        static final class Deserializer extends StaticFromStringDeserializer<TestObject> {

            public Deserializer() {
                super(TestObject.class, TestObject::fromString);
            }
        }
    }
}
