package org.example.age.testing.json;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.example.age.common.api.AgeRange;
import org.junit.jupiter.api.Test;

public final class TestObjectMapperTest {

    @Test
    public void get() {
        ObjectMapper mapper = TestObjectMapper.get();
        assertThat(mapper).isNotNull();
        assertThat(mapper).isSameAs(TestObjectMapper.get());
    }

    @Test
    public void serializeNonNull() throws IOException {
        AgeRange ageRange = AgeRange.builder().min(18).build();
        String json = TestObjectMapper.get().writeValueAsString(ageRange);
        assertThat(json).isEqualTo("{\"min\":18}");
    }
}
