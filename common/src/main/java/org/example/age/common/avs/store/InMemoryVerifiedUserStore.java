package org.example.age.common.avs.store;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;

/** In-memory {@link VerifiedUserStore}. */
@Singleton
final class InMemoryVerifiedUserStore implements VerifiedUserStore {

    private final Map<String, VerifiedUser> users = new HashMap<>();
    private final BiMap<SecureId, String> verifiedAccountIds = HashBiMap.create();

    private final Object lock = new Object();

    @Inject
    public InMemoryVerifiedUserStore(@Named("initializer") Optional<Consumer<VerifiedUserStore>> maybeInitializer) {
        maybeInitializer.ifPresent(initializer -> initializer.accept(this));
    }

    @Override
    public Optional<VerifiedUser> tryLoad(String accountId) {
        synchronized (lock) {
            return Optional.ofNullable(users.get(accountId));
        }
    }

    @Override
    public Optional<String> trySave(String accountId, VerifiedUser user) {
        synchronized (lock) {
            Optional<String> maybeDuplicateAccountId = checkNoDuplicateVerifications(accountId, user);
            if (maybeDuplicateAccountId.isPresent()) {
                return maybeDuplicateAccountId;
            }

            users.put(accountId, user);
            verifiedAccountIds.put(user.pseudonym(), accountId);
        }
        return Optional.empty();
    }

    @Override
    public void delete(String accountId) {
        synchronized (lock) {
            users.remove(accountId);
            verifiedAccountIds.inverse().remove(accountId);
        }
    }

    /** Checks that two accounts are not verified with the same {@link VerifiedUser}. */
    private Optional<String> checkNoDuplicateVerifications(String accountId, VerifiedUser user) {
        Optional<String> maybeVerifiedAccountId = Optional.ofNullable(verifiedAccountIds.get(user.pseudonym()));
        if (maybeVerifiedAccountId.isEmpty()) {
            return Optional.empty();
        }

        String verifiedAccountId = maybeVerifiedAccountId.get();
        return verifiedAccountId.equals(accountId) ? Optional.empty() : Optional.of(verifiedAccountId);
    }
}
