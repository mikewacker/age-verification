package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.testing.json.JsonTester;
import org.junit.jupiter.api.Test;

public final class BytesValueTest {

    @Test
    public void empty() {
        BytesValue value = BytesValue.empty();
        assertThat(value.bytes()).hasSize(0);
    }

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(BytesValue.ofBytes(new byte[] {1, 2, 3, 4}), new TypeReference<>() {});
    }
}
