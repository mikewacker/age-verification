package org.example.age.avs.provider.userlocalizer.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.common.api.crypto.SecureId;
import org.immutables.value.Value;

/** Configuration for the localization key. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableSiteLocalizationKeyConfig.class)
public interface SiteLocalizationKeyConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Key. */
    SecureId key();

    /** Builder for the configuration. */
    final class Builder extends ImmutableSiteLocalizationKeyConfig.Builder {

        Builder() {}
    }
}
