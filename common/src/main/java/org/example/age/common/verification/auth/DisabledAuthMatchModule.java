package org.example.age.common.verification.auth;

import dagger.Binds;
import dagger.Module;

/** Publishes a binding for {@link AuthMatchDataExtractor} that always returns a successful match. */
@Module
public interface DisabledAuthMatchModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(DisabledAuthMatchDataExtractor impl);
}
