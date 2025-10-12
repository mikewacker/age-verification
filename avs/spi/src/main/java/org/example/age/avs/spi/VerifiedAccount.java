package org.example.age.avs.spi;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.common.annotation.ValueStyle;
import org.example.age.common.api.VerifiedUser;
import org.immutables.value.Value;

/** Verified account that is linked to pseudonymous user data. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableVerifiedAccount.class)
public interface VerifiedAccount {

    /** Creates a builder for the account. */
    static Builder builder() {
        return new Builder();
    }

    /** Account ID. */
    String id();

    /** Pseudonymous user data. */
    VerifiedUser user();

    /** Builder for the account. */
    final class Builder extends ImmutableVerifiedAccount.Builder {

        Builder() {}
    }
}
