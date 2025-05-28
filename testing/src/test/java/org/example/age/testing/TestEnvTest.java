package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Component;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import org.example.age.api.AgeCertificate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestEnvTest {

    private static ObjectMapper mapper;
    private static ExecutorService worker;

    @BeforeAll
    public static void createEnvironment() {
        TestComponent component = TestComponent.create();
        mapper = component.objectMapper();
        worker = component.worker();
    }

    @Test
    public void env() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        String json = await(CompletableFuture.supplyAsync(() -> serialize(ageCertificate), worker));
        assertThat(json).isNotNull();
    }

    private static String serialize(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Dagger component for the environment. */
    @Component(modules = TestEnvModule.class)
    @Singleton
    public interface TestComponent {

        static TestComponent create() {
            return DaggerTestEnvTest_TestComponent.create();
        }

        ObjectMapper objectMapper();

        @Named("worker")
        ExecutorService worker();
    }
}
