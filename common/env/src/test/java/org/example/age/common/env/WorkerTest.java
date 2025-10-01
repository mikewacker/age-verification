package org.example.age.common.env;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.util.WebStageTesting.await;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.testing.env.TestLiteEnvModule;
import org.junit.jupiter.api.Test;

public final class WorkerTest {

    private static final Worker worker = TestComponent.create();

    @Test
    public void dispatch() {
        int value = await(worker.dispatch(() -> 1));
        assertThat(value).isEqualTo(1);
    }

    /** Dagger component for {@link Worker}. */
    @Component(modules = {EnvModule.class, TestLiteEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<Worker> {

        static Worker create() {
            return DaggerWorkerTest_TestComponent.create().get();
        }
    }
}
