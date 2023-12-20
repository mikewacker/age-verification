package org.example.age.service.crypto.internal;

import org.example.age.data.certificate.SignedAgeCertificate;

/** Verifies the signature of a {@link SignedAgeCertificate}. */
@FunctionalInterface
public interface AgeCertificateVerifier {

    boolean verify(SignedAgeCertificate signedCertificate);
}
