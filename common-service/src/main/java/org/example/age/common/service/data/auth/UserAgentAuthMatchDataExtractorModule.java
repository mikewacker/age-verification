package org.example.age.common.service.data.auth;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.data.auth.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataExtractor},
 * which matches the {@code User-Agent} header, or sends a 401 error if decryption fails.
 */
@Module
public interface UserAgentAuthMatchDataExtractorModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(UserAgentAuthMatchDataExtractor impl);
}
