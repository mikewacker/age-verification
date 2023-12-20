package org.example.age.module.extractor.demo.common;

import dagger.Binds;
import dagger.Module;
import org.example.age.api.extractor.common.AccountIdExtractor;

/**
 * Dagger module that publishes a binding for {@link AccountIdExtractor},
 * which reads the custom {@code Account-Id} header, or returns a 401 error.
 */
@Module
public interface DemoAccountIdExtractorModule {

    @Binds
    AccountIdExtractor bindAccountIdExtractor(DemoAccountIdExtractor impl);
}
