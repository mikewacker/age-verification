package org.example.age.service.crypto.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.service.key.RefreshablePrivateSigningKeyProvider;

@Singleton
final class AgeCertificateSignerImpl implements AgeCertificateSigner {

    private final RefreshablePrivateSigningKeyProvider keyProvider;

    @Inject
    public AgeCertificateSignerImpl(RefreshablePrivateSigningKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public SignedAgeCertificate sign(AgeCertificate certificate) {
        return SignedAgeCertificate.sign(certificate, keyProvider.getPrivateSigningKey());
    }
}
