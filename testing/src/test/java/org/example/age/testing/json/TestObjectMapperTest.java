package org.example.age.testing.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.io.IOException;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.crypto.SecureId;
import org.junit.jupiter.api.Test;

public final class TestObjectMapperTest {

    @Test
    public void get() {
        ObjectMapper mapper = TestObjectMapper.get();
        assertThat(mapper).isNotNull();
        assertThat(mapper).isSameAs(TestObjectMapper.get());
    }

    @Test
    public void serializeThenDeserialize() {
        SecureId id = SecureId.generate();
        String json = TestObjectMapper.serialize(id);
        SecureId rtId = TestObjectMapper.deserialize(json, SecureId.class);
        assertThat(rtId).isEqualTo(id);
    }

    @Test
    public void serializeNonNull() throws IOException {
        AgeRange ageRange = AgeRange.builder().min(18).build();
        String json = TestObjectMapper.get().writeValueAsString(ageRange);
        assertThat(json).isEqualTo("{\"min\":18}");
    }

    @Test
    public void error_UnrecognizedProperty() {
        String json = "{\"min\":13,\"max\":18,\"dne\":0}";
        assertThatThrownBy(() -> TestObjectMapper.get().readValue(json, AgeRange.class))
                .isInstanceOf(UnrecognizedPropertyException.class);
    }
}
