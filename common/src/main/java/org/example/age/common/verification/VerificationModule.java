package org.example.age.common.verification;

import dagger.Binds;
import dagger.Module;
import java.time.Duration;
import java.util.function.Supplier;
import org.example.age.common.verification.auth.AuthMatchDataExtractor;

/**
 * Publishes a binding for {@link VerificationStateManager}.
 *
 * <p>It depends on an unbound {@link VerifiedUserStore} and {@link AuthMatchDataExtractor}.
 * It also depends on an unbound <code>@Named("expiresIn") {@link Supplier}&lt;{@link Duration}&gt;</code>,
 * which determines when age verification expires.</p>
 */
@Module
public interface VerificationModule {

    @Binds
    VerificationStateManager bindVerificationStateManager(VerificationStateManagerImpl impl);
}
