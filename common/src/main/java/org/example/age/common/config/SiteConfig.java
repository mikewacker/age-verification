package org.example.age.common.config;

import com.google.common.net.HostAndPort;
import java.security.PublicKey;
import java.time.Duration;
import org.example.age.data.SecureId;
import org.example.age.internal.PackageImplementation;
import org.immutables.value.Value;

/** Configuration for a site. */
@Value.Immutable
@PackageImplementation
public interface SiteConfig {

    /** Creates a builder for the site configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Host and port of the age verification service. */
    HostAndPort avsHostAndPort();

    /** Public signing key used to verify signed age certificates. */
    PublicKey avsPublicSigningKey();

    /** Site ID that is expected for age certificates. */
    String siteId();

    /** Key used to localize pseudonyms. */
    SecureId pseudonymKey();

    /** Expiration for verified accounts. */
    Duration expiresIn();

    class Builder extends ImmutableSiteConfig.Builder {}
}
