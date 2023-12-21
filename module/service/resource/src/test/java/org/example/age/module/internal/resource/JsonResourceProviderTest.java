package org.example.age.module.internal.resource;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public final class JsonResourceProviderTest {

    @Test
    public void get() {
        TestResourceProvider provider = TestResourceProvider.create();
        String text = provider.get();
        assertThat(text).isEqualTo("test");
    }

    /** Test {@link JsonResourceProvider}. */
    private static final class TestResourceProvider extends JsonResourceProvider<String> {

        public static TestResourceProvider create() {
            return new TestResourceProvider(ResourceLoaderTest.TestComponent.createResourceLoader());
        }

        public String get() {
            return getInternal();
        }

        private TestResourceProvider(ResourceLoader resourceLoader) {
            super(resourceLoader, Path.of("test/test.json"), new TypeReference<>() {});
        }
    }
}
