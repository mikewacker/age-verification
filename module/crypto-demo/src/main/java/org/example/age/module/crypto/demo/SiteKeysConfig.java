package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.api.SiteApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.api.config.ConfigStyle;
import org.immutables.value.Value;

/**
 * Configuration for the keys used by the service implementation of {@link SiteApi}.
 * <p>
 * It suffices to say that a real-world application should NOT store keys in configuration.
 */
@Value.Immutable
@ConfigStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableSiteKeysConfig.class)
public interface SiteKeysConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Key used to localize pseudonyms. */
    SecureId localization();

    /** Public key used to verify the signature for an age certificate. */
    EccPublicKey signing();

    /** Builder for the configuration. */
    final class Builder extends ImmutableSiteKeysConfig.Builder {

        Builder() {}
    }
}
