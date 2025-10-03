package org.example.age.common.env;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.testing.env.TestEnvModule;
import org.junit.jupiter.api.Test;

public final class JsonTest {

    private static final JsonMapper mapper = TestComponent.create();

    @Test
    public void serializeThenDeserialize() {
        String json = mapper.serialize("test");
        String value = mapper.deserialize(json, String.class);
        assertThat(value).isEqualTo("test");
    }

    /** Dagger component for {@link JsonMapper}. */
    @Component(modules = {BaseEnvModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<JsonMapper> {

        static JsonMapper create() {
            return DaggerJsonTest_TestComponent.create().get();
        }
    }
}
