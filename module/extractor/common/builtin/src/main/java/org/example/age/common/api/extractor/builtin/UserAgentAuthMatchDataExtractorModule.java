package org.example.age.common.api.extractor.builtin;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.extractor.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataExtractor},
 * which matches the {@code User-Agent} header.
 */
@Module
public interface UserAgentAuthMatchDataExtractorModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(UserAgentAuthMatchDataExtractor impl);
}
