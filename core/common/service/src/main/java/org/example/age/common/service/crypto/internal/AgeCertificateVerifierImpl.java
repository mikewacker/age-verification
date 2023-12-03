package org.example.age.common.service.crypto.internal;

import java.security.PublicKey;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.data.certificate.SignedAgeCertificate;

@Singleton
final class AgeCertificateVerifierImpl implements AgeCertificateVerifier {

    private final Provider<PublicKey> publicSigningKeyProvider;

    @Inject
    public AgeCertificateVerifierImpl(@Named("signing") Provider<PublicKey> publicSigningKeyProvider) {
        this.publicSigningKeyProvider = publicSigningKeyProvider;
    }

    @Override
    public boolean verify(SignedAgeCertificate signedCertificate) {
        return signedCertificate.verify(publicSigningKeyProvider.get());
    }
}
