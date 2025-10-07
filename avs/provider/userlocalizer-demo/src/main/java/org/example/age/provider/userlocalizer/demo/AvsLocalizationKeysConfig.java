package org.example.age.provider.userlocalizer.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.common.api.crypto.SecureId;
import org.immutables.value.Value;

/** Configuration for the localization keys. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableAvsLocalizationKeysConfig.class)
public interface AvsLocalizationKeysConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Keys, keyed by site ID. */
    Map<String, SecureId> keys();

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsLocalizationKeysConfig.Builder {

        Builder() {}
    }
}
