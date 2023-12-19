package org.example.age.module.extractor.builtin.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.age.api.def.common.AuthMatchData;
import org.example.age.data.json.JsonValues;
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
        byte[] rawAuthData = JsonValues.serialize(authData);
        AuthMatchData rtAuthData = JsonValues.deserialize(rawAuthData, new TypeReference<>() {});
        assertThat(rtAuthData).isEqualTo(authData);
    }
}
