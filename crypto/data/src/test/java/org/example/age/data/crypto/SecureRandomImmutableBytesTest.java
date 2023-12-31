package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import org.junit.jupiter.api.Test;

public final class SecureRandomImmutableBytesTest {

    @Test
    public void generate() {
        TestValue value = TestValue.generate();
        assertThat(value.bytes()).hasSize(32);
        assertThat(value.bytes()).isNotEqualTo(new byte[32]);
    }

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(TestValue.generate(), new TypeReference<>() {});
    }

    /** Test implementations of {@link SecureRandomImmutableBytes}. */
    public static final class TestValue extends SecureRandomImmutableBytes {

        private static final int EXPECTED_LENGTH = 32;

        public static TestValue ofBytes(byte[] value) {
            return new TestValue(value, true);
        }

        public static TestValue generate() {
            return new TestValue();
        }

        @JsonCreator
        static TestValue ofUncopiedBytes(byte[] value) {
            return new TestValue(value, false);
        }

        private TestValue(byte[] value, boolean copy) {
            super(value, copy, EXPECTED_LENGTH);
        }

        private TestValue() {
            super(EXPECTED_LENGTH);
        }
    }
}
