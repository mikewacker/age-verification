package org.example.age.service.module.env;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.api.crypto.SecureId;
import org.example.age.testing.TestEnvModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class EnvUtilsTest {

    private static EnvUtils utils;

    @BeforeAll
    public static void createEnvUtils() {
        TestComponent component = TestComponent.create();
        utils = component.envUtils();
    }

    @Test
    public void serializeThenDeserialize() {
        SecureId id = SecureId.generate();
        String json = utils.serialize(id);
        SecureId rtId = utils.deserialize(json, SecureId.class);
        assertThat(rtId).isEqualTo(id);
    }

    @Test
    public void runAsync() {
        CompletionStage<Integer> asyncTask = utils.runAsync(() -> 1);
        assertThat(asyncTask).isCompletedWithValue(1);
    }

    /** Dagger component for the environment. */
    @Component(modules = TestEnvModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerEnvUtilsTest_TestComponent.create();
        }

        EnvUtils envUtils();
    }
}
