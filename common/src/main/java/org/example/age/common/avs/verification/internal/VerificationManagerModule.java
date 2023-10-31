package org.example.age.common.avs.verification.internal;

import dagger.Binds;
import dagger.Module;

/** Dagger module that publishes a binding for {@link VerificationManager}. */
@Module
public interface VerificationManagerModule {

    @Binds
    VerificationManager bindVerificationManager(VerificationManagerImpl impl);
}
