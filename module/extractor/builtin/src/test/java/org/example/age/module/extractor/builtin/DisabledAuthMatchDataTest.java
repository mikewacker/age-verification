package org.example.age.module.extractor.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import org.example.age.api.def.AuthMatchData;
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
        JsonTester.serializeThenDeserialize(DisabledAuthMatchData.of(), new TypeReference<>() {});
    }
}
