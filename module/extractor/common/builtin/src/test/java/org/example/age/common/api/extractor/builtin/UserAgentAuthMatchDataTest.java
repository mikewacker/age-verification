package org.example.age.common.api.extractor.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.example.age.common.api.data.AuthMatchData;
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
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AuthMatchData authData = UserAgentAuthMatchData.of("agent");
        byte[] rawAuthData = mapper.writeValueAsBytes(authData);
        AuthMatchData rtAuthData = mapper.readValue(rawAuthData, new TypeReference<>() {});
        assertThat(rtAuthData).isEqualTo(authData);
    }
}
