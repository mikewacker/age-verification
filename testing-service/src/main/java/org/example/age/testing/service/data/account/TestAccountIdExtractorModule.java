package org.example.age.testing.service.data.account;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.data.account.AccountIdExtractor;

/**
 * Dagger module that publishes a binding for {@link AccountIdExtractor},
 * which reads the custom {@code Account-Id} header, or sends a 401 error.
 */
@Module
public interface TestAccountIdExtractorModule {

    @Binds
    AccountIdExtractor bindAccountIdExtractor(TestAccountIdExtractor impl);
}
