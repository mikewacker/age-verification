package org.example.age.service.location.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.data.json.JsonValues;
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
        Location location = Location.of("localhost", 80);
        byte[] rawLocation = JsonValues.serialize(location);
        Location rtLocation = JsonValues.deserialize(rawLocation, new TypeReference<>() {});
        assertThat(rtLocation).isEqualTo(location);
    }
}
