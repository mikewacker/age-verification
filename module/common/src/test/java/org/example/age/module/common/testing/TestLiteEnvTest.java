package org.example.age.module.common.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import dagger.Component;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.example.age.module.common.LiteEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestLiteEnvTest {

    private static LiteEnv env;

    @BeforeAll
    public static void createEnv() {
        TestComponent component = TestComponent.create();
        env = component.liteEnv();
    }

    @Test
    public void env() {
        String json = await(CompletableFuture.supplyAsync(() -> serialize("test"), env.worker()));
        assertThat(json).isEqualTo("\"test\"");
    }

    private static String serialize(Object value) {
        try {
            return env.jsonMapper().writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Dagger component for the environment. */
    @Component(modules = TestLiteEnvModule.class)
    @Singleton
    public interface TestComponent {

        static TestComponent create() {
            return DaggerTestLiteEnvTest_TestComponent.create();
        }

        LiteEnv liteEnv();
    }
}
