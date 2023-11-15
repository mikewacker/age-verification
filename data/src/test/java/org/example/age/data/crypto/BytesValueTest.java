package org.example.age.data.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class BytesValueTest {

    @Test
    public void empty() {
        BytesValue value = BytesValue.empty();
        assertThat(value.bytes()).isEqualTo(new byte[1]);
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BytesValue value = BytesValue.ofBytes(new byte[] {116, 101, 115, 116});
        byte[] rawValue = mapper.writeValueAsBytes(value);
        BytesValue rtValue = mapper.readValue(rawValue, new TypeReference<>() {});
        assertThat(rtValue).isEqualTo(value);
    }
}
