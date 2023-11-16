package org.example.age.common.service.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.age.common.api.data.AuthMatchData;
import org.junit.jupiter.api.Test;

public final class UserAgentAuthMatchDataTest {

    @Test
    public void match_Matches() {
        AuthMatchData data1 = UserAgentAuthMatchData.of("agent");
        AuthMatchData data2 = UserAgentAuthMatchData.of("agent");
        boolean matches = data1.match(data2);
        assertThat(matches).isTrue();
    }

    @Test
    public void match_DoesNotMatch() {
        AuthMatchData data1 = UserAgentAuthMatchData.of("agent1");
        AuthMatchData data2 = UserAgentAuthMatchData.of("agent2");
        boolean matches = data1.match(data2);
        assertThat(matches).isFalse();
    }
}
