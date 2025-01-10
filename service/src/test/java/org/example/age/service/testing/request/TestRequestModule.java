package org.example.age.service.testing.request;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.request.AccountIdContext;

/**
 * Dagger modules that binds {@link AccountIdContext}.
 * <p>
 * It also binds {@link TestAccountId}.
 */
@Module
public interface TestRequestModule {

    @Binds
    AccountIdContext bindAccountIdContext(FakeAccountIdContext impl);
}
