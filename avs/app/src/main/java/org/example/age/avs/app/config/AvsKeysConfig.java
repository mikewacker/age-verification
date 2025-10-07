package org.example.age.avs.app.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.avs.provider.userlocalizer.demo.AvsLocalizationKeysConfig;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.common.provider.signingkey.demo.EcPrivateKeyConfig;
import org.immutables.value.Value;

/** Configuration for the keys. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableAvsKeysConfig.class)
public interface AvsKeysConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Configuration for the private signing key. */
    EcPrivateKeyConfig signing();

    /** Configuration for the localization keys. */
    AvsLocalizationKeysConfig localization();

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsKeysConfig.Builder {

        Builder() {}
    }
}
