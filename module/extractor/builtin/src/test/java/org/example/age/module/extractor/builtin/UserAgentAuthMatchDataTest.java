package org.example.age.module.extractor.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import org.example.age.api.def.AuthMatchData;
import org.junit.jupiter.api.Test;

public final class UserAgentAuthMatchDataTest {

    @Test
    public void match_Matches() {
        AuthMatchData authData1 = UserAgentAuthMatchData.of("agent");
        AuthMatchData authData2 = UserAgentAuthMatchData.of("agent");
        boolean matches = authData1.match(authData2);
        assertThat(matches).isTrue();
    }

    @Test
    public void match_DoesNotMatch() {
        AuthMatchData authData1 = UserAgentAuthMatchData.of("agent1");
        AuthMatchData authData2 = UserAgentAuthMatchData.of("agent2");
        boolean matches = authData1.match(authData2);
        assertThat(matches).isFalse();
    }

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(UserAgentAuthMatchData.of("agent"), new TypeReference<>() {});
    }
}
