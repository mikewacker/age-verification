package org.example.age.common.api.extractor.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.api.JsonSerializer;
import org.example.age.common.api.data.AuthMatchData;
import org.junit.jupiter.api.Test;

public final class DisabledAuthMatchDataTest {

    @Test
    public void match() {
        AuthMatchData authData1 = DisabledAuthMatchData.of();
        AuthMatchData authData2 = DisabledAuthMatchData.of();
        boolean matches = authData1.match(authData2);
        assertThat(matches).isTrue();
    }

    @Test
    public void serializeThenDeserialize() {
        AuthMatchData authData = DisabledAuthMatchData.of();
        byte[] rawAuthData = JsonSerializer.serialize(authData);
        AuthMatchData rtAuthData = JsonSerializer.deserialize(rawAuthData, new TypeReference<>() {});
        assertThat(rtAuthData).isEqualTo(authData);
    }
}
