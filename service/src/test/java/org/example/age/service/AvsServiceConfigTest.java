package org.example.age.service;

import java.io.IOException;
import org.example.age.service.testing.TestConfig;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AvsServiceConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        JsonTesting.serializeThenDeserialize(TestConfig.avs(), AvsServiceConfig.class);
    }
}
