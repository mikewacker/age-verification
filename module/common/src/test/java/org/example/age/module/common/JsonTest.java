package org.example.age.module.common;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.module.common.testing.TestLiteEnvModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class JsonTest {

    private static JsonMapper mapper;

    @BeforeAll
    public static void createJsonMapper() {
        TestComponent component = TestComponent.create();
        mapper = component.jsonMapper();
    }

    @Test
    public void serializeThenDeserialize() {
        String json = mapper.serialize("test");
        String value = mapper.deserialize(json, String.class);
        assertThat(value).isEqualTo("test");
    }

    /** Dagger component for the environment. */
    @Component(modules = {CommonModule.class, TestLiteEnvModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerJsonTest_TestComponent.create();
        }

        JsonMapper jsonMapper();
    }
}
