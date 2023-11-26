package org.example.age.test.common.service.crypto;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.service.crypto.PseudonymKeyProvider;

/** Dagger module that publishes a binding for {@link PseudonymKeyProvider}. */
@Module
public interface TestPseudonymKeyModule {

    @Binds
    PseudonymKeyProvider bindPseudonymKeyProvider(TestPseudonymKeyStore impl);
}
