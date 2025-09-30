package org.example.age.module.store.redis.testing;

import org.example.age.testing.api.TestModels;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RedisTestContainerTest {

    @RegisterExtension
    private static final RedisTestContainer redis = new RedisTestContainer();

    @Test
    public void setUpContainer() {
        redis.createAvsAccount("person", TestModels.createVerifiedUser());
    }
}
