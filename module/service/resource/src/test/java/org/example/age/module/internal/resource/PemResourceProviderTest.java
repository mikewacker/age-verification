package org.example.age.module.internal.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.junit.jupiter.api.Test;

public final class PemResourceProviderTest {

    @Test
    public void get() {
        TestResourceProvider keyProvider = TestResourceProvider.create();
        PrivateKeyInfo key = keyProvider.get();
        assertThat(key).isNotNull();
    }

    /** Test {@link PemResourceProvider}. */
    private static final class TestResourceProvider extends PemResourceProvider<PrivateKeyInfo> {

        public static TestResourceProvider create() {
            return new TestResourceProvider(TestResourceComponent.createResourceLoader());
        }

        public PrivateKeyInfo get() {
            return getInternal();
        }

        @Override
        protected PrivateKeyInfo createKey(Object pemObject) {
            return (PrivateKeyInfo) pemObject;
        }

        private TestResourceProvider(ResourceLoader resourceLoader) {
            super(resourceLoader, Path.of("test/test.pem"));
        }
    }
}
