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

    private final Provider<PrivateKey> privateKeyProvider;

    @Inject
    public AgeCertificateSignerImpl(@Named("signing") Provider<PrivateKey> privateKeyProvider) {
        this.privateKeyProvider = privateKeyProvider;
    }

    @Override
    public SignedAgeCertificate sign(AgeCertificate certificate) {
        return SignedAgeCertificate.sign(certificate, privateKeyProvider.get());
    }
}
