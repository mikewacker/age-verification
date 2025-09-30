package org.example.age.api.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.testing.EqualsTester;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.example.age.common.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class ImmutableBytesTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        TestBytes o = TestBytes.of("Hello, world!".getBytes(StandardCharsets.UTF_8));
        JsonTesting.serializeThenDeserialize(o, TestBytes.class);
    }

    @Test
    public void encodeInUrlFriendlyFormat() {
        TestBytes o = new TestBytes(new byte[] {-5, -16, 0, 0}); // 4 bytes would be padded if padding is used
        assertThat(o.toString()).isEqualTo("-_AAAA"); // '-' and '_' are URL-friendly, and no padding exists
    }

    @Test
    public void equals() {
        TestBytes o1 = TestBytes.of(new byte[] {0, 1, 2, 3});
        TestBytes o2 = TestBytes.of(new byte[] {0, 1, 2, 3});
        TestBytes o3 = TestBytes.of(new byte[] {4, 5, 6, 7});
        ImmutableBytes o4 = new ImmutableBytes(new byte[] {0, 1, 2, 3}) {}; // same bytes, different class
        new EqualsTester()
                .addEqualityGroup(o1, o2)
                .addEqualityGroup(o3)
                .addEqualityGroup(o4)
                .testEquals();
    }

    @Test
    public void copyOnRead() {
        TestBytes o1 = TestBytes.of(new byte[] {1, 2, 3, 4});
        byte[] modifiedBytes = o1.getBytes();
        modifiedBytes[0] = 0;
        assertThat(o1.getBytes()).isNotEqualTo(modifiedBytes);
    }

    /** Test implementation of {@link ImmutableBytes}. */
    private static final class TestBytes extends ImmutableBytes {

        static TestBytes of(byte[] bytes) {
            byte[] copiedBytes = Arrays.copyOf(bytes, bytes.length);
            return new TestBytes(copiedBytes);
        }

        @JsonCreator
        static TestBytes fromString(String text) {
            byte[] bytes = bytesFromString(text);
            return new TestBytes(bytes);
        }

        private TestBytes(byte[] bytes) {
            super(bytes);
        }
    }
}
