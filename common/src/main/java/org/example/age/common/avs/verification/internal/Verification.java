package org.example.age.common.avs.verification.internal;

import com.google.common.net.HostAndPort;
import org.example.age.data.certificate.AgeCertificate;
import org.immutables.value.Value;

/** An {@link AgeCertificate} and a location to send it to. */
@Value.Immutable
public interface Verification {

    static Verification of(AgeCertificate certificate, HostAndPort location) {
        return ImmutableVerification.builder()
                .ageCertificate(certificate)
                .siteLocation(location)
                .build();
    }

    /** Age certificate to send. */
    AgeCertificate ageCertificate();

    /** Location to send the age certificate to. */
    HostAndPort siteLocation();
}
