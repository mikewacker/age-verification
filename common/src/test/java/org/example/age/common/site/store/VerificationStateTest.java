package org.example.age.common.site.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert;
import org.example.age.data.VerifiedUser;
import org.example.age.data.crypto.SecureId;
import org.junit.jupiter.api.Test;

public final class VerificationStateTest {

    private static final VerifiedUser VERIFIED_USER = VerifiedUser.of(SecureId.generate(), 18);
    private static final long EXPIRATION = System.currentTimeMillis() / 1000;

    @Test
    public void unverified() {
        VerificationState state = VerificationState.unverified();
        assertThat(state.status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void verified() {
        VerificationState state = VerificationState.verified(VERIFIED_USER, EXPIRATION);
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.verifiedUser()).isEqualTo(VERIFIED_USER);
        assertThat(state.expiration()).isEqualTo(EXPIRATION);
    }

    @Test
    public void expired() {
        VerificationState state = VerificationState.expired(EXPIRATION);
        assertThat(state.status()).isEqualTo(VerificationStatus.EXPIRED);
        assertThat(state.expiration()).isEqualTo(EXPIRATION);
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
        long future = EXPIRATION + 10;
        VerificationState state = VerificationState.verified(VERIFIED_USER, future);
        VerificationState updatedState = state.update();
        assertThat(updatedState).isSameAs(state);
    }

    @Test
    public void update_VerifiedButExpired() {
        long past = EXPIRATION - 10;
        VerificationState state = VerificationState.verified(VERIFIED_USER, past);
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
        VerificationState state = VerificationState.expired(EXPIRATION);
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
}
