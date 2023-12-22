package org.example.age.module.key.resource;

import java.nio.file.Path;
import java.security.PrivateKey;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.example.age.module.internal.resource.PemResourceProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.service.key.RefreshablePrivateSigningKeyProvider;

/** {@link RefreshablePrivateSigningKeyProvider} that gets the key from a resource file. Is not refreshable. */
@Singleton
final class ResourcePrivateSigningKeyProvider extends PemResourceProvider<PrivateKey>
        implements RefreshablePrivateSigningKeyProvider {

    @Inject
    public ResourcePrivateSigningKeyProvider(ResourceLoader resourceLoader, @Named("resources") Path rootPath) {
        super(resourceLoader, rootPath.resolve("key/privateSigningKey.pem"));
    }

    @Override
    public PrivateKey getPrivateSigningKey() {
        return getInternal();
    }

    @Override
    protected PrivateKey createKey(Object pemObject) {
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        try {
            return converter.getPrivateKey((PrivateKeyInfo) pemObject);
        } catch (PEMException e) {
            throw new RuntimeException(e);
        }
    }
}
