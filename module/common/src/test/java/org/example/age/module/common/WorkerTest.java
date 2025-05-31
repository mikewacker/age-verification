package org.example.age.module.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.module.common.testing.TestLiteEnvModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class WorkerTest {

    private static Worker worker;

    @BeforeAll
    public static void createWorker() {
        TestComponent component = TestComponent.create();
        worker = component.worker();
    }

    @Test
    public void dispatch() {
        int value = await(worker.dispatch(() -> 1));
        assertThat(value).isEqualTo(1);
    }

    /** Dagger component for the environment. */
    @Component(modules = {CommonModule.class, TestLiteEnvModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerWorkerTest_TestComponent.create();
        }

        Worker worker();
    }
}
