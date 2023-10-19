package org.example.age.common.auth;

import dagger.Binds;
import dagger.Module;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataExtractor},
 * which always returns a successful match.
 */
@Module
public interface DisabledAuthMatchDataExtractorModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(DisabledAuthMatchDataExtractor impl);
}
