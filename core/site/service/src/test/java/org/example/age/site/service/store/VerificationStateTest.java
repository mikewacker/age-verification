package org.example.age.site.service.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class VerificationStateTest {

    private static VerifiedUser user;

    @BeforeAll
    public static void createVerifiedUser() {
        user = VerifiedUser.of(SecureId.generate(), 18);
    }

    @Test
    public void unverified() {
        VerificationState state = VerificationState.unverified();
        assertThat(state.status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void verified() {
        long expiration = createExpiration();
        VerificationState state = VerificationState.verified(user, expiration);
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.verifiedUser()).isEqualTo(user);
        assertThat(state.expiration()).isEqualTo(expiration);
    }

    @Test
    public void expired() {
        long expiration = createExpiration();
        VerificationState state = VerificationState.expired(expiration);
        assertThat(state.status()).isEqualTo(VerificationStatus.EXPIRED);
        assertThat(state.expiration()).isEqualTo(expiration);
    }

    @Test
    public void invalidated() {
        VerificationState state = VerificationState.invalidated();
        assertThat(state.status()).isEqualTo(VerificationStatus.INVALIDATED);
    }

    @Test
    public void update_Unverified() {
        VerificationState state = VerificationState.unverified();
        VerificationState updatedState = state.update();
        assertThat(updatedState).isSameAs(state);
    }

    @Test
    public void update_VerifiedAndNotExpired() {
        long future = createExpiration(10);
        VerificationState state = VerificationState.verified(user, future);
        VerificationState updatedState = state.update();
        assertThat(updatedState).isSameAs(state);
    }

    @Test
    public void update_VerifiedButExpired() {
        long past = createExpiration(-10);
        VerificationState state = VerificationState.verified(user, past);
        VerificationState updatedState = state.update();
        assertThat(updatedState.status()).isEqualTo(VerificationStatus.EXPIRED);
        assertThat(updatedState.expiration()).isEqualTo(past);
    }

    @Test
    public void error_AttributeNotSet_Unverified() {
        String message = "attribute not set when the status is UNVERIFIED";
        VerificationState state = VerificationState.unverified();
        error_AttributeNotSet(state::verifiedUser, message);
        error_AttributeNotSet(state::expiration, message);
    }

    @Test
    public void error_AttributeNotSet_Expired() {
        String message = "attribute not set when the status is EXPIRED";
        long expiration = createExpiration();
        VerificationState state = VerificationState.expired(expiration);
        error_AttributeNotSet(state::verifiedUser, message);
    }

    @Test
    public void error_AttributeNotSet_Invalidated() {
        String message = "attribute not set when the status is INVALIDATED";
        VerificationState state = VerificationState.invalidated();
        error_AttributeNotSet(state::verifiedUser, message);
        error_AttributeNotSet(state::expiration, message);
    }

    private void error_AttributeNotSet(ThrowableAssert.ThrowingCallable callable, String message) {
        assertThatThrownBy(callable).isInstanceOf(IllegalStateException.class).hasMessage(message);
    }

    private static long createExpiration() {
        return createExpiration(10);
    }

    private static long createExpiration(long expiresIn) {
        long now = System.currentTimeMillis() / 1000;
        return now + expiresIn;
    }
}
