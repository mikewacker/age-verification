package org.example.age.common.verification;

import io.undertow.server.HttpServerExchange;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;

/**
 * Test in-memory user store that gets the account ID from the {@code Account-Id} header.
 *
 * <p>It does not handle invalidated accounts that were previously verified.</p>
 */
@Singleton
final class TestVerifiedUserStore implements VerifiedUserStore {

    private final Map<String, VerificationState> states = new HashMap<>();
    private final Map<SecureId, String> accountIdByPseudonym = new HashMap<>();

    @Inject
    public TestVerifiedUserStore() {}

    @Override
    public Optional<String> tryGetAccountId(HttpServerExchange exchange) {
        return Optional.ofNullable(exchange.getRequestHeaders().getFirst("Account-Id"));
    }

    @Override
    public Optional<String> tryGetAccountId(VerifiedUser user) {
        return Optional.ofNullable(accountIdByPseudonym.get(user.pseudonym()));
    }

    @Override
    public VerificationState load(String accountId) {
        return states.getOrDefault(accountId, VerificationState.unverified());
    }

    @Override
    public void save(String accountId, VerificationState state) {
        states.put(accountId, state);
        if (state.status() == VerificationStatus.VERIFIED) {
            accountIdByPseudonym.put(state.verifiedUser().pseudonym(), accountId);
        }
    }
}
