package org.example.age.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.testing.EqualsTester;
import java.util.Arrays;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class SecureIdTest {

    private static final byte[] ID_BYTES = new byte[] {
        -31, 98, -36, -70, -19, 26, 103, 100, -116, -60, 47, 69, 97, 84, 30, -128, -85, -5, -46, 46, 53, -24, 47, -28,
        -11, -11, -92, 98, 54, 33, 66, 80
    };
    private static final String ID_STRING = "4WLcuu0aZ2SMxC9FYVQegKv70i416C_k9fWkYjYhQlA";

    private static ObjectMapper mapper;

    @BeforeAll
    public static void createMapper() {
        mapper = new ObjectMapper();
    }

    @Test
    public void generate() {
        SecureId id = SecureId.generate();
        assertThat(id.getBytes()).hasSize(32);
        assertThat(id.toString()).hasSize(43);
    }

    @Test
    public void localize() {
        SecureId id = SecureId.generate();
        SecureId key = SecureId.generate();
        SecureId localId = id.localize(key);
        assertThat(localId).isNotEqualTo(id);
        assertThat(localId.getBytes()).hasSize(32);
        assertThat(localId.toString()).hasSize(43);
    }

    @Test
    public void toString_() {
        SecureId id = SecureId.ofBytes(ID_BYTES);
        assertThat(id.toString()).isEqualTo(ID_STRING);
    }

    @Test
    public void fromString() {
        SecureId id = SecureId.fromString(ID_STRING);
        assertThat(id.getBytes()).isEqualTo(ID_BYTES);
    }

    @Test
    public void serializeThenDeserialize() throws JsonProcessingException {
        SecureId id = SecureId.generate();
        String json = mapper.writeValueAsString(id);
        SecureId deserializedId = mapper.readValue(json, SecureId.class);
        assertThat(deserializedId).isEqualTo(id);
    }

    @Test
    public void equals() {
        SecureId id1 = SecureId.generate();
        SecureId id2 = SecureId.ofBytes(id1.getBytes());
        SecureId id3 = SecureId.generate();
        new EqualsTester().addEqualityGroup(id1, id2).addEqualityGroup(id3).testEquals();
    }

    @Test
    public void copyOnCreate() {
        byte[] bytes = Arrays.copyOf(ID_BYTES, ID_BYTES.length);
        SecureId id = SecureId.ofBytes(bytes);
        bytes[0] = 0;
        assertThat(id.getBytes()[0]).isNotEqualTo(bytes[0]);
    }

    @Test
    public void copyOnRead() {
        SecureId id = SecureId.ofBytes(Arrays.copyOf(ID_BYTES, ID_BYTES.length));
        byte[] bytes = id.getBytes();
        bytes[0] = 0;
        assertThat(id.getBytes()[0]).isNotEqualTo(bytes[0]);
    }

    @Test
    public void error_IllegalLength_OfBytes() {
        error_IllegalLength(() -> SecureId.ofBytes(new byte[4]));
    }

    @Test
    public void error_IllegalLength_FromString() {
        error_IllegalLength(() -> SecureId.fromString("123456"));
    }

    private void error_IllegalLength(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("secure ID must have 256 bits");
    }
}
