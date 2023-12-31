package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.testing.EqualsTester;
import io.github.mikewacker.drift.testing.json.JsonTester;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public final class ImmutableBytesTest {

    private static final byte[] BYTES = new byte[] {-5, -16, 0, 0};

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(TestValue.ofBytes(BYTES), new TypeReference<>() {});
    }

    @Test
    public void equals() {
        new EqualsTester()
                .addEqualityGroup(TestValue.ofBytes(BYTES), TestValue.ofBytes(BYTES))
                .addEqualityGroup(TestValue.ofBytes(new byte[8]))
                .addEqualityGroup(new ImmutableBytes(BYTES, false) {})
                .testEquals();
    }

    @Test
    public void toString_() {
        TestValue value = TestValue.ofBytes(BYTES);
        assertThat(value.toString()).isEqualTo("-_AAAA");
    }

    @Test
    public void copyOnCreate() {
        byte[] bytes = Arrays.copyOf(BYTES, BYTES.length);
        TestValue value = TestValue.ofBytes(bytes);
        bytes[0] = 0;
        assertThat(value.bytes()).isNotEqualTo(bytes);
    }

    @Test
    public void copyOnRead() {
        TestValue value = TestValue.ofBytes(BYTES);
        byte[] bytes = value.bytes();
        bytes[0] = 0;
        assertThat(value.bytes()).isNotEqualTo(bytes);
    }

    /** Test implementation of {@link ImmutableBytes}. */
    public static final class TestValue extends ImmutableBytes {

        public static TestValue ofBytes(byte[] value) {
            return new TestValue(value, true);
        }

        @JsonCreator
        static TestValue ofUncopiedBytes(byte[] value) {
            return new TestValue(value, false);
        }

        private TestValue(byte[] value, boolean copy) {
            super(value, copy);
        }
    }
}
