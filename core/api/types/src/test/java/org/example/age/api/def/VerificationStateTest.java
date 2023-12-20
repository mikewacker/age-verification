package org.example.age.api.def;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.testing.json.JsonTester;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class VerificationStateTest {

    private static VerifiedUser user;
    private static long expiration;

    @BeforeAll
    public static void createVerifiedUserAndExpiration() {
        user = VerifiedUser.of(SecureId.generate(), 18);
        long now = System.currentTimeMillis() / 1000;
        expiration = now + Duration.ofDays(30).toSeconds();
    }

    @Test
    public void unverified() {
        VerificationState state = VerificationState.unverified();
        assertThat(state.status()).isEqualTo(VerificationStatus.UNVERIFIED);
        assertThat(state.verifiedUser()).isNull();
        assertThat(state.expiration()).isNull();
    }

    @Test
    public void verified() {
        VerificationState state = VerificationState.verified(user, expiration);
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.verifiedUser()).isEqualTo(user);
        assertThat(state.expiration()).isEqualTo(expiration);
    }

    @Test
    public void expired() {
        VerificationState state = VerificationState.expired(expiration);
        assertThat(state.status()).isEqualTo(VerificationStatus.EXPIRED);
        assertThat(state.verifiedUser()).isNull();
        assertThat(state.expiration()).isEqualTo(expiration);
    }

    @Test
    public void update_Unverified() {
        VerificationState state = VerificationState.unverified();
        VerificationState updatedState = state.update();
        assertThat(updatedState).isSameAs(state);
    }

    @Test
    public void update_VerifiedAndNotExpired() {
        VerificationState state = VerificationState.verified(user, expiration);
        VerificationState updatedState = state.update();
        assertThat(updatedState).isSameAs(state);
    }

    @Test
    public void update_VerifiedButExpired() {
        long now = System.currentTimeMillis() / 1000;
        long past = now - 10;
        VerificationState state = VerificationState.verified(user, past);
        VerificationState updatedState = state.update();
        assertThat(updatedState.status()).isEqualTo(VerificationStatus.EXPIRED);
        assertThat(updatedState.expiration()).isEqualTo(past);
    }

    @Test
    public void serializeThenDeserialize_Unverified() {
        JsonTester.serializeThenDeserialize(VerificationState.unverified(), new TypeReference<>() {});
    }

    @Test
    public void serializeThenDeserialize_Verified() {
        JsonTester.serializeThenDeserialize(VerificationState.verified(user, expiration), new TypeReference<>() {});
    }

    @Test
    public void serializeThenDeserialize_Expired() {
        JsonTester.serializeThenDeserialize(VerificationState.expired(expiration), new TypeReference<>() {});
    }
}
