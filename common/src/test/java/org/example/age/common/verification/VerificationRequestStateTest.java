package org.example.age.common.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.assertj.core.api.ThrowableAssert;
import org.example.age.certificate.AuthToken;
import org.example.age.certificate.VerificationRequest;
import org.junit.jupiter.api.Test;

public final class VerificationRequestStateTest {

    private static final VerificationRequest REQUEST =
            VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
    private static final AuthToken TOKEN = AuthToken.empty();

    @Test
    public void inactive() {
        VerificationRequestState state = VerificationRequestState.inactive();
        assertThat(state.status()).isEqualTo(VerificationRequestStatus.INACTIVE);
    }

    @Test
    public void pending() {
        VerificationRequestState state = VerificationRequestState.pending(REQUEST, TOKEN);
        assertThat(state.status()).isEqualTo(VerificationRequestStatus.PENDING);
        assertThat(state.verificationRequest()).isEqualTo(REQUEST);
        assertThat(state.localAuthToken()).isEqualTo(TOKEN);
    }

    @Test
    public void expired() {
        VerificationRequestState state = VerificationRequestState.expired(REQUEST);
        assertThat(state.status()).isEqualTo(VerificationRequestStatus.EXPIRED);
        assertThat(state.verificationRequest()).isEqualTo(REQUEST);
    }

    @Test
    public void update_Inactive() {
        VerificationRequestState state = VerificationRequestState.inactive();
        VerificationRequestState updatedState = state.update();
        assertThat(updatedState).isSameAs(state);
    }

    @Test
    public void update_PendingAndNotExpired() {
        VerificationRequestState state = VerificationRequestState.pending(REQUEST, TOKEN);
        VerificationRequestState updatedState = state.update();
        assertThat(updatedState).isSameAs(state);
    }

    @Test
    public void update_PendingButExpired() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(-1));
        VerificationRequestState state = VerificationRequestState.pending(request, TOKEN);
        VerificationRequestState updatedState = state.update();
        assertThat(updatedState.status()).isEqualTo(VerificationRequestStatus.EXPIRED);
        assertThat(updatedState.verificationRequest()).isSameAs(state.verificationRequest());
    }

    @Test
    public void error_AttributeNotSet_Inactive() {
        String message = "attribute not set when the status is INACTIVE";
        VerificationRequestState state = VerificationRequestState.inactive();
        error_AttributeNotSet(() -> state.verificationRequest(), message);
        error_AttributeNotSet(() -> state.localAuthToken(), message);
    }

    @Test
    public void error_AttributeNotSet_Expired() {
        String message = "attribute not set when the status is EXPIRED";
        VerificationRequestState state = VerificationRequestState.expired(REQUEST);
        error_AttributeNotSet(() -> state.localAuthToken(), message);
    }

    private void error_AttributeNotSet(ThrowableAssert.ThrowingCallable callable, String message) {
        assertThatThrownBy(callable).isInstanceOf(IllegalStateException.class).hasMessage(message);
    }
}
