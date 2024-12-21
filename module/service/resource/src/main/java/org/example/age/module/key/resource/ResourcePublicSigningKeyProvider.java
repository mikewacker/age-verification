package org.example.age.module.key.resource;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.example.age.module.internal.resource.PemResourceProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.service.key.RefreshablePublicSigningKeyProvider;

/** {@link RefreshablePublicSigningKeyProvider} that gets the key from a resource file. Is not refreshable. */
@Singleton
final class ResourcePublicSigningKeyProvider extends PemResourceProvider<PublicKey>
        implements RefreshablePublicSigningKeyProvider {

    @Inject
    public ResourcePublicSigningKeyProvider(ResourceLoader resourceLoader, @Named("resources") Path rootPath) {
        super(resourceLoader, rootPath.resolve("key/publicSigningKey.pem"));
    }

    @Override
    public PublicKey getPublicSigningKey() {
        return getInternal();
    }

    @Override
    protected PublicKey createKey(Object pemObject) {
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        try {
            return converter.getPublicKey((SubjectPublicKeyInfo) pemObject);
        } catch (PEMException e) {
            throw new RuntimeException(e);
        }
    }
}
