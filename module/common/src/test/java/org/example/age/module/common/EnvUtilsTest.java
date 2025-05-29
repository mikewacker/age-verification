package org.example.age.module.common;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.module.common.testing.TestLiteEnvModule;
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
        String json = utils.serialize("test");
        String value = utils.deserialize(json, String.class);
        assertThat(value).isEqualTo("test");
    }

    @Test
    public void runAsync() {
        CompletionStage<Integer> asyncTask = utils.runAsync(() -> 1);
        assertThat(asyncTask).isCompletedWithValue(1);
    }

    /** Dagger component for the environment. */
    @Component(modules = {CommonModule.class, TestLiteEnvModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerEnvUtilsTest_TestComponent.create();
        }

        EnvUtils envUtils();
    }
}
