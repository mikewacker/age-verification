package org.example.age.module.crypto.demo.keys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.PublicKey;
import org.example.age.api.SiteApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.common.util.ValueStyle;
import org.immutables.value.Value;

/**
 * Configuration for the keys used by the service implementation of {@link SiteApi}.
 * <p>
 * It suffices to say that a production application should NOT store keys in configuration.
 */
@Value.Immutable
@ValueStyle
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

    /** Public key used to verify the signature for an age certificate. */
    @Value.Derived
    @JsonIgnore
    default PublicKey signingJca() {
        return NistP256Keys.toPublicKey(signing());
    }

    /** Builder for the configuration. */
    final class Builder extends ImmutableSiteKeysConfig.Builder {

        Builder() {}
    }
}
