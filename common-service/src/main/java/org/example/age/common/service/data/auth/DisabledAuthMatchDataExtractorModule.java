package org.example.age.common.service.data.auth;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.data.auth.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataExtractor},
 * which always returns a successful match.
 */
@Module
public interface DisabledAuthMatchDataExtractorModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(DisabledAuthMatchDataExtractor impl);
}
