package org.example.age.common.service.crypto.internal;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.service.key.RefreshableKeyProvider;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;

@Singleton
final class VerifiedUserLocalizerImpl implements VerifiedUserLocalizer {

    private final RefreshableKeyProvider keyProvider;

    @Inject
    public VerifiedUserLocalizerImpl(RefreshableKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public VerifiedUser localize(VerifiedUser user, String keyName) {
        SecureId pseudonymKey = keyProvider.getPseudonymKey(keyName);
        return user.localize(pseudonymKey);
    }
}
