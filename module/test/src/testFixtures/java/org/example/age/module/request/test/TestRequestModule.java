package org.example.age.module.request.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.spi.AccountIdContext;

/**
 * Dagger modules that binds {@link AccountIdContext}.
 * <p>
 * The account ID is set via {@link TestAccountId}, which is also bound.
 */
@Module
public interface TestRequestModule {

    @Binds
    AccountIdContext bindAccountIdContext(FakeAccountIdContext impl);
}
