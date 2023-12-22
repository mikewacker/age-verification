package org.example.age.module.internal.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class ResourceLoaderTest {

    private static ResourceLoader resourceLoader;

    @BeforeAll
    public static void createResourceLoader() {
        resourceLoader = TestResourceComponent.createResourceLoader();
    }

    @Test
    public void loadJson() {
        String text = resourceLoader.loadJson(Path.of("test/test.json"), new TypeReference<>() {});
        assertThat(text).isEqualTo("test");
    }

    @Test
    public void loadPem() {
        PrivateKeyInfo key = (PrivateKeyInfo) resourceLoader.loadPem(Path.of("test/test.pem"));
        assertThat(key).isNotNull();
    }

    @Test
    public void error_FileNotFound() {
        assertThatThrownBy(() -> resourceLoader.loadJson(Path.of("dne.json"), new TypeReference<String>() {}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("resource not found: dne.json");
    }
}
