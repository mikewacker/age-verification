package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import org.example.age.api.AvsApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.api.config.ConfigStyle;
import org.immutables.value.Value;

/**
 * Configuration for the keys used by the service implementation of {@link AvsApi}.
 * <p>
 * It suffices to say that a production application should NOT store keys in configuration.
 */
@Value.Immutable
@ConfigStyle
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

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsKeysConfig.Builder {

        Builder() {}
    }
}
