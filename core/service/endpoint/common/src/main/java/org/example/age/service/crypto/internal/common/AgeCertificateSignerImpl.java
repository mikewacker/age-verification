package org.example.age.service.crypto.internal.common;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.service.module.key.common.RefreshableKeyProvider;

@Singleton
final class AgeCertificateSignerImpl implements AgeCertificateSigner {

    private final RefreshableKeyProvider keyProvider;

    @Inject
    public AgeCertificateSignerImpl(RefreshableKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public SignedAgeCertificate sign(AgeCertificate certificate) {
        return SignedAgeCertificate.sign(certificate, keyProvider.getPrivateSigningKey());
    }
}
