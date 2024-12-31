package org.example.age.service.testing.request;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.api.request.AccountIdExtractor;

/**
 * Dagger modules that binds {@link AccountIdExtractor}.
 * <p>
 * It also binds {@link TestAccountId}.
 */
@Module
public interface TestRequestModule {

    @Binds
    AccountIdExtractor bindAccountIdExtractor(FakeAccountIdExtractor impl);
}
