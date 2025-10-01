package org.example.age.service.module.crypto;

import java.util.concurrent.CompletionStage;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;

/** Signs an age certificate. */
@FunctionalInterface
public interface AgeCertificateSigner {

    CompletionStage<SignedAgeCertificate> sign(AgeCertificate ageCertificate);
}
