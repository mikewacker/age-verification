package org.example.age.service.common.crypto.internal;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link AuthMatchDataEncryptor}. */
@Module
public interface AuthMatchDataEncryptorModule {

    @Binds
    AuthMatchDataEncryptor bindAuthMatchDataEncryptor(AuthMatchDataEncryptorImpl impl);
}
