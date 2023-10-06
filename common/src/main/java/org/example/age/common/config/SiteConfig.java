package org.example.age.common.config;

import com.google.common.net.HostAndPort;
import java.security.PublicKey;
import java.time.Duration;
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
    PublicKey avsPublicKey();

    /** Site ID that is expected for age certificates. */
    String siteId();

    /** Expiration for verified accounts. */
    Duration expiresIn();

    class Builder extends ImmutableSiteConfig.Builder {}
}
