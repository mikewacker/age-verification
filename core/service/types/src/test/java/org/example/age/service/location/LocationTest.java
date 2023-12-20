package org.example.age.service.location;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.testing.json.JsonTester;
import org.junit.jupiter.api.Test;

public final class LocationTest {

    @Test
    public void urls() {
        Location location = Location.of("localhost", 80);
        assertThat(location.rootUrl()).isEqualTo("http://localhost:80");
        assertThat(location.url("/path?name=%s", "value")).isEqualTo("http://localhost:80/path?name=value");
        assertThat(location.apiUrl("/path?name=%s", "value")).isEqualTo("http://localhost:80/api/path?name=value");
    }

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(Location.of("localhost", 80), new TypeReference<>() {});
    }
}
