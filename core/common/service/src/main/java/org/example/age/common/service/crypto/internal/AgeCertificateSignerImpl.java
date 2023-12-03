package org.example.age.common.service.crypto.internal;

import java.security.PrivateKey;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;

@Singleton
final class AgeCertificateSignerImpl implements AgeCertificateSigner {

    private final Provider<PrivateKey> privateSigningKeyProvider;

    @Inject
    public AgeCertificateSignerImpl(@Named("signing") Provider<PrivateKey> privateSigningKeyProvider) {
        this.privateSigningKeyProvider = privateSigningKeyProvider;
    }

    @Override
    public SignedAgeCertificate sign(AgeCertificate certificate) {
        return SignedAgeCertificate.sign(certificate, privateSigningKeyProvider.get());
    }
}
