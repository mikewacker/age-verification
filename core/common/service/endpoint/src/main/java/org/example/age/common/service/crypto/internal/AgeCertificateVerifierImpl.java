package org.example.age.common.service.crypto.internal;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.service.key.RefreshableKeyProvider;
import org.example.age.data.certificate.SignedAgeCertificate;

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
