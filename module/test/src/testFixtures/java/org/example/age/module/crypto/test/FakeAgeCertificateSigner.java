package org.example.age.module.crypto.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeCertificate;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.service.module.crypto.AgeCertificateSigner;
import org.example.age.testing.api.TestSignatures;

/** Fake implementation of {@link AgeCertificateSigner}. */
@Singleton
final class FakeAgeCertificateSigner implements AgeCertificateSigner {

    @Inject
    public FakeAgeCertificateSigner() {}

    @Override
    public CompletionStage<SignedAgeCertificate> sign(AgeCertificate ageCertificate) {
        SignedAgeCertificate signedAgeCertificate = TestSignatures.sign(ageCertificate);
        return CompletableFuture.completedFuture(signedAgeCertificate);
    }
}
