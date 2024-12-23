package org.example.age.module.store.test;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.List;
import org.example.age.api.def.VerificationState;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.module.store.inmemory.VerificationStoreInitializer;
import org.example.age.service.store.VerificationStore;

@Singleton
final class TestAvsVerificationStoreInitializer implements VerificationStoreInitializer {

    @Inject
    public TestAvsVerificationStoreInitializer() {}

    @Override
    public void initialize(VerificationStore store) {
        VerifiedUser parent = VerifiedUser.of(SecureId.generate(), 40);
        VerifiedUser child = VerifiedUser.of(SecureId.generate(), 13, List.of(parent.pseudonym()));
        long expiration = createExpiration();
        VerificationState parentState = VerificationState.verified(parent, expiration);
        VerificationState childState = VerificationState.verified(child, expiration);
        store.trySave("John Smith", parentState);
        store.trySave("Billy Smith", childState);
    }

    /** Creates an expiration timestamp in seconds. */
    private static long createExpiration() {
        long now = System.currentTimeMillis() / 1000;
        return now + Duration.ofDays(30).toSeconds();
    }
}
