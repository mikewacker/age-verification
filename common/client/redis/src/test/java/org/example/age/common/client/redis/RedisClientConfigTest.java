package org.example.age.common.client.redis;

import org.example.age.testing.config.TestConfigParser;
import org.junit.jupiter.api.Test;

public final class RedisClientConfigTest {

    @Test
    public void parse() throws Exception {
        TestConfigParser.forClass(RedisClientConfig.class).parseLines("uri: http://localhost:6379");
    }
}
