package org.example.age.module.crypto.demo.keys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.PrivateKey;
import java.util.Map;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.common.api.crypto.SecureId;
import org.immutables.value.Value;

/**
 * Configuration for the keys used by the service implementation of {@code AvsApi}.
 * <p>
 * It suffices to say that a production application should NOT store keys in configuration.
 */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableAvsKeysConfig.class)
public interface AvsKeysConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Keys used to localize pseudonyms for each site, keyed by site ID. */
    Map<String, SecureId> localization();

    /** Private key used to sign age certificates. */
    EccPrivateKey signing();

    /** Private key used to sign age certificates. */
    @Value.Derived
    @JsonIgnore
    default PrivateKey signingJca() {
        return NistP256Keys.toPrivateKey(signing());
    }

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsKeysConfig.Builder {

        Builder() {}
    }
}
