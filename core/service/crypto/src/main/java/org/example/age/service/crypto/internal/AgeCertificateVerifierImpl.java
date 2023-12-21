package org.example.age.service.crypto.internal;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.service.key.RefreshableKeyProvider;

@Singleton
final class AgeCertificateVerifierImpl implements AgeCertificateVerifier {

    private final RefreshableKeyProvider keyProvider;

    @Inject
    public AgeCertificateVerifierImpl(RefreshableKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public boolean verify(SignedAgeCertificate signedCertificate) {
        return signedCertificate.verify(keyProvider.getPublicSigningKey());
    }
}