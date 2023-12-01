package org.example.age.common.api.extractor.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.extractor.AccountIdExtractor;

/**
 * Dagger module that publishes a binding for {@link AccountIdExtractor},
 * which reads the custom {@code Account-Id} header, or returns a 401 error.
 */
@Module
public interface TestAccountIdExtractorModule {

    @Binds
    AccountIdExtractor bindAccountIdExtractor(TestAccountIdExtractor impl);
}
