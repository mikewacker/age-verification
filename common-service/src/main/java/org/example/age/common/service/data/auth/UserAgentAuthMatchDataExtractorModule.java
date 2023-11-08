package org.example.age.common.service.data.auth;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.data.auth.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataExtractor},
 * which matches the {@code User-Agent} header.
 */
@Module
public interface UserAgentAuthMatchDataExtractorModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(UserAgentAuthMatchDataExtractor impl);
}
