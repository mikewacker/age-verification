package org.example.age.common.service.crypto.internal;

import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;

/** Signs an {@link AgeCertificate}. */
@FunctionalInterface
public interface AgeCertificateSigner {

    SignedAgeCertificate sign(AgeCertificate certificate);
}
