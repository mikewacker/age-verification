package org.example.age.service.api.crypto;

import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeCertificate;
import org.example.age.api.SignedAgeCertificate;

/** Signs an age certificate. */
@FunctionalInterface
public interface AgeCertificateSigner {

    CompletionStage<SignedAgeCertificate> sign(AgeCertificate ageCertificate);
}
