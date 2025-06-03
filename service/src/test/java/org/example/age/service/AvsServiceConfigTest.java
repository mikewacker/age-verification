package org.example.age.service;

import java.io.IOException;
import org.example.age.common.testing.JsonTesting;
import org.example.age.service.testing.TestConfig;
import org.junit.jupiter.api.Test;

public final class AvsServiceConfigTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        JsonTesting.serializeThenDeserialize(TestConfig.avsService(), AvsServiceConfig.class);
    }
}
