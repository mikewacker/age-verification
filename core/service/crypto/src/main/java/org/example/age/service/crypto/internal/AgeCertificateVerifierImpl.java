package org.example.age.service.crypto.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.service.key.RefreshablePublicSigningKeyProvider;

@Singleton
final class AgeCertificateVerifierImpl implements AgeCertificateVerifier {

    private final RefreshablePublicSigningKeyProvider keyProvider;

    @Inject
    public AgeCertificateVerifierImpl(RefreshablePublicSigningKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public boolean verify(SignedAgeCertificate signedCertificate) {
        return signedCertificate.verify(keyProvider.getPublicSigningKey());
    }
}
