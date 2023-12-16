package org.example.age.module.extractor.common.builtin;

import dagger.Binds;
import dagger.Module;
import org.example.age.api.module.extractor.common.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataExtractor},
 * which matches the {@code User-Agent} header.
 */
@Module
public interface UserAgentAuthMatchDataExtractorModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(UserAgentAuthMatchDataExtractor impl);
}
