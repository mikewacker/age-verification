package org.example.age.common.avs.config;

import java.security.PrivateKey;
import java.time.Duration;
import org.example.age.data.utils.DataStyle;
import org.immutables.value.Value;

/** Configuration for the age verification service. */
@Value.Immutable
@DataStyle
public interface AvsConfig {

    /** Creates a builder for the AVS configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Private key used to sign age certificates. */
    PrivateKey privateSigningKey();

    /** Expiration for verification sessions. */
    Duration expiresIn();

    final class Builder extends ImmutableAvsConfig.Builder {}
}
