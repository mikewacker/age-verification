package org.example.age.common.site.store;

import org.example.age.data.user.VerifiedUser;

/** Current state for age verification. Some fields may be conditionally present based on the status. */
public final class VerificationState {

    private final VerificationStatus status;
    private final VerifiedUser verifiedUser;
    private final Long expiration;

    /** Creates an unverified state. */
    public static VerificationState unverified() {
        return new VerificationState(VerificationStatus.UNVERIFIED, null, null);
    }

    /** Creates a verified state. */
    public static VerificationState verified(VerifiedUser verifiedUser, long expiration) {
        return new VerificationState(VerificationStatus.VERIFIED, verifiedUser, expiration);
    }

    /** Creates an expired state. */
    public static VerificationState expired(long expiration) {
        return new VerificationState(VerificationStatus.EXPIRED, null, expiration);
    }

    /** Creates an invalidated state. */
    public static VerificationState invalidated() {
        return new VerificationState(VerificationStatus.INVALIDATED, null, null);
    }

    /** Gets the current verification status. */
    public VerificationStatus status() {
        return status;
    }

    /** Gets the verified user, once the user is verified. */
    public VerifiedUser verifiedUser() {
        return checkAttributeSet(verifiedUser);
    }

    /** Gets the epoch time when the user's verified status expired or will expire. */
    public long expiration() {
        return checkAttributeSet(expiration);
    }

    /** Updates the state based on the current time, returning the updated state. */
    public VerificationState update() {
        if (status != VerificationStatus.VERIFIED) {
            return this;
        }

        long now = System.currentTimeMillis() / 1000;
        if (now < expiration) {
            return this;
        }

        return VerificationState.expired(expiration);
    }

    /** Checks that the attribute is set for the current status. */
    private <T> T checkAttributeSet(T attribute) {
        if (attribute == null) {
            String message = String.format("attribute not set when the status is %s", status);
            throw new IllegalStateException(message);
        }

        return attribute;
    }

    private VerificationState(VerificationStatus status, VerifiedUser verifiedUser, Long expiration) {
        this.status = status;
        this.verifiedUser = verifiedUser;
        this.expiration = expiration;
    }
}
