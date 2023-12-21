package org.example.age.module.internal.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import java.nio.file.Path;
import javax.inject.Singleton;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class ResourceLoaderTest {

    private static ResourceLoader resourceLoader;

    @BeforeAll
    public static void createResourceLoader() {
        resourceLoader = TestComponent.createResourceLoader();
    }

    @Test
    public void loadJson() {
        String text = resourceLoader.loadJson(Path.of("test/test.json"), new TypeReference<>() {});
        assertThat(text).isEqualTo("test");
    }

    @Test
    public void error_FileNotFound() {
        assertThatThrownBy(() -> resourceLoader.loadJson(Path.of("dne.json"), new TypeReference<String>() {}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("resource not found: dne.json");
    }

    /** Dagger component that provides a {@link ResourceLoader}. */
    @Component(modules = {ResourceLoaderModule.class, TestSiteResourceModule.class})
    @Singleton
    interface TestComponent {

        static ResourceLoader createResourceLoader() {
            TestComponent component = DaggerResourceLoaderTest_TestComponent.create();
            return component.resourceLoader();
        }

        ResourceLoader resourceLoader();
    }
}
