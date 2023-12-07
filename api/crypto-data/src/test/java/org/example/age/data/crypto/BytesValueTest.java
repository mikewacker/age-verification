package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.api.JsonObjects;
import org.junit.jupiter.api.Test;

public class BytesValueTest {

    @Test
    public void empty() {
        BytesValue value = BytesValue.empty();
        assertThat(value.bytes()).hasSize(0);
    }

    @Test
    public void serializeThenDeserialize() {
        BytesValue value = BytesValue.ofBytes(new byte[] {1, 2, 3, 4});
        byte[] rawValue = JsonObjects.serialize(value);
        BytesValue rtValue = JsonObjects.deserialize(rawValue, new TypeReference<>() {});
        assertThat(rtValue).isEqualTo(value);
    }
}
