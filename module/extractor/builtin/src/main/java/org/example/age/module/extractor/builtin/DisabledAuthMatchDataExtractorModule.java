package org.example.age.module.extractor.builtin;

import dagger.Binds;
import dagger.Module;
import org.example.age.api.extractor.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataExtractor}, which always returns a successful match.
 */
@Module
public interface DisabledAuthMatchDataExtractorModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(DisabledAuthMatchDataExtractor impl);
}
