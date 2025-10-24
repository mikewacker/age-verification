package org.example.age.site.app.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.common.provider.signingkey.demo.EcPublicKeyConfig;
import org.example.age.site.provider.userlocalizer.demo.SiteLocalizationKeyConfig;
import org.immutables.value.Value;

/** Configuration for the keys. */
@Value.Immutable
@ValueStyle
@JsonDeserialize(as = ImmutableSiteKeysConfig.class)
public interface SiteKeysConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Configuration for the public signing key. */
    EcPublicKeyConfig signing();

    /** Configuration for the localization key. */
    SiteLocalizationKeyConfig localization();

    /** Builder for the configuration. */
    final class Builder extends ImmutableSiteKeysConfig.Builder {

        Builder() {}
    }
}
