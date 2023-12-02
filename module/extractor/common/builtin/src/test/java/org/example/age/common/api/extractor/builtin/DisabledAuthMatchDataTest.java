package org.example.age.common.api.extractor.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AuthMatchData authData = DisabledAuthMatchData.of();
        byte[] rawAuthData = mapper.writeValueAsBytes(authData);
        AuthMatchData rtAuthData = mapper.readValue(rawAuthData, new TypeReference<>() {});
        assertThat(rtAuthData).isEqualTo(authData);
    }
}
