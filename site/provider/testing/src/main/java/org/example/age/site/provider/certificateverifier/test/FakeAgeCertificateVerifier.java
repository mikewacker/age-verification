package org.example.age.site.provider.certificateverifier.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.site.spi.AgeCertificateVerifier;
import org.example.age.testing.api.TestSignatures;

/** Fake implementation of {@link AgeCertificateVerifier}. */
@Singleton
final class FakeAgeCertificateVerifier implements AgeCertificateVerifier {

    @Inject
    public FakeAgeCertificateVerifier() {}

    @Override
    public CompletionStage<AgeCertificate> verify(SignedAgeCertificate signedAgeCertificate) {
        try {
            AgeCertificate ageCertificate = TestSignatures.verify(signedAgeCertificate);
            return CompletableFuture.completedFuture(ageCertificate);
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(new NotAuthorizedException(e));
        }
    }
}
