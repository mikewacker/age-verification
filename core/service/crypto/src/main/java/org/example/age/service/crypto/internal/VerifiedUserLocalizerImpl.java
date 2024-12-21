package org.example.age.service.crypto.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;

@Singleton
final class VerifiedUserLocalizerImpl implements VerifiedUserLocalizer {

    private final RefreshablePseudonymKeyProvider keyProvider;

    @Inject
    public VerifiedUserLocalizerImpl(RefreshablePseudonymKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public VerifiedUser localize(VerifiedUser user, String keyName) {
        SecureId pseudonymKey = keyProvider.getPseudonymKey(keyName);
        return user.localize(pseudonymKey);
    }
}
