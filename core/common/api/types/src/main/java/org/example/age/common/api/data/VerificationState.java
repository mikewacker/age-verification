package org.example.age.common.api.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;
import org.example.age.api.ApiStyle;
import org.example.age.data.user.VerifiedUser;
import org.immutables.value.Value;

/** Current state for age verification. Some fields may be conditionally present based on the status. */
@Value.Immutable
@ApiStyle
@JsonDeserialize(as = ImmutableVerificationState.class)
public interface VerificationState {

    /** Creates an unverified state. */
    static VerificationState unverified() {
        return ImmutableVerificationState.builder()
                .status(VerificationStatus.UNVERIFIED)
                .build();
    }

    /** Creates a verified state with an expiration timestamp (in seconds). */
    static VerificationState verified(VerifiedUser user, long expiration) {
        return ImmutableVerificationState.builder()
                .status(VerificationStatus.VERIFIED)
                .verifiedUser(Objects.requireNonNull(user, "user"))
                .expiration(expiration)
                .build();
    }

    /** Creates an expired state with an expiration timestamp (in seconds). */
    static VerificationState expired(long expiration) {
        return ImmutableVerificationState.builder()
                .status(VerificationStatus.EXPIRED)
                .expiration(expiration)
                .build();
    }

    /** Creates an invalidated state. */
    static VerificationState invalidated() {
        return ImmutableVerificationState.builder()
                .status(VerificationStatus.INVALIDATED)
                .build();
    }

    /** Gets the current verification status. */
    VerificationStatus status();

    /** Gets the verified user if the account is verified. */
    @Nullable
    VerifiedUser verifiedUser();

    /** Gets the time (as a timestamp in seconds) when the account's verified status expired or will expire. */
    @Nullable
    Long expiration();

    /** Updates the state based on the current time, returning the updated state. */
    @Value.Auxiliary
    default VerificationState update() {
        if (status() != VerificationStatus.VERIFIED) {
            return this;
        }

        long now = System.currentTimeMillis() / 1000;
        if (now <= expiration()) {
            return this;
        }

        return VerificationState.expired(expiration());
    }
}
