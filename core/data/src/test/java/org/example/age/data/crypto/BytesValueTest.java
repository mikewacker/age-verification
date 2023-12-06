package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.charset.StandardCharsets;
import org.example.age.api.JsonSerializer;
import org.junit.jupiter.api.Test;

public class BytesValueTest {

    private static final byte[] BYTES = "Hello, world!".getBytes(StandardCharsets.UTF_8);

    @Test
    public void empty() {
        BytesValue value = BytesValue.empty();
        assertThat(value.bytes()).isEqualTo(new byte[1]);
    }

    @Test
    public void serializeThenDeserialize() {
        BytesValue value = BytesValue.ofBytes(BYTES);
        byte[] rawValue = JsonSerializer.serialize(value);
        BytesValue rtValue = JsonSerializer.deserialize(rawValue, new TypeReference<>() {});
        assertThat(rtValue).isEqualTo(value);
    }
}
