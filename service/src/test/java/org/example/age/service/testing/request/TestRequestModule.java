package org.example.age.service.testing.request;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.request.AccountIdContext;

/**
 * Dagger modules that binds...
 * <ul>
 *     <li>{@link AccountIdContext}
 *     <li>{@link TestAccountId}
 * </ul>
 */
@Module
public interface TestRequestModule {

    @Binds
    AccountIdContext bindAccountIdContext(FakeAccountIdContext impl);
}
