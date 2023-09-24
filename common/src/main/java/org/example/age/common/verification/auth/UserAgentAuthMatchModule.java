package org.example.age.common.verification.auth;

import dagger.Binds;
import dagger.Module;

/** Publishes a binding for {@link AuthMatchDataExtractor} that matches the {@code User-Agent} header. */
@Module
public interface UserAgentAuthMatchModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(UserAgentAuthMatchDataExtractor impl);
}
