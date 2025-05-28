package org.example.age.common.testing;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public final class TestObjectMapperTest {

    @Test
    public void get() {
        ObjectMapper mapper = TestObjectMapper.get();
        assertThat(mapper).isNotNull();
        assertThat(mapper).isSameAs(TestObjectMapper.get());
    }
}
