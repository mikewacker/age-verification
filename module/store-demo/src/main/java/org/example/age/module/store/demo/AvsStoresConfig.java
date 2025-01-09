package org.example.age.module.store.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import org.example.age.api.AvsApi;
import org.example.age.api.VerifiedUser;
import org.example.age.service.api.config.ConfigStyle;
import org.immutables.value.Value;

/** Configuration for the stores used by the service implementation of {@link AvsApi}. */
@Value.Immutable
@ConfigStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableAvsStoresConfig.class)
public interface AvsStoresConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** Accounts whose age (and guardians, if applicable) have been verified. */
    Map<String, VerifiedUser> verifiedAccounts();

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsStoresConfig.Builder {

        Builder() {}
    }
}
