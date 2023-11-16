package org.example.age.common.service.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.age.common.api.data.AuthMatchData;
import org.junit.jupiter.api.Test;

public final class DisabledAuthMatchDataTest {

    @Test
    public void match() {
        AuthMatchData data1 = DisabledAuthMatchData.of();
        AuthMatchData data2 = DisabledAuthMatchData.of();
        boolean matches = data1.match(data2);
        assertThat(matches).isTrue();
    }
}
