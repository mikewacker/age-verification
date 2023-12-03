package org.example.age.common.service.crypto.internal;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.service.key.PseudonymKeyProvider;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;

@Singleton
final class VerifiedUserLocalizerImpl implements VerifiedUserLocalizer {

    private final PseudonymKeyProvider pseudonymKeyProvider;

    @Inject
    public VerifiedUserLocalizerImpl(PseudonymKeyProvider pseudonymKeyProvider) {
        this.pseudonymKeyProvider = pseudonymKeyProvider;
    }

    @Override
    public VerifiedUser localize(VerifiedUser user, String keyName) {
        SecureId pseudonymKey = pseudonymKeyProvider.get(keyName);
        return user.localize(pseudonymKey);
    }
}
