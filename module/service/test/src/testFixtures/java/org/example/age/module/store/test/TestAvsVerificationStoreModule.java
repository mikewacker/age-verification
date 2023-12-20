package org.example.age.module.store.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.store.inmemory.InMemoryVerificationStoreModule;
import org.example.age.module.store.inmemory.VerificationStoreInitializer;
import org.example.age.service.store.VerificationStore;

/**
 * Dagger module that binds dependencies for {@link VerificationStore},
 * which is an in-memory store without persistence.
 *
 * <p>It populates the store with accounts for {@code "John Smith"} (parent) and {@code "Billy Smith"} (child).</p>
 */
@Module(includes = InMemoryVerificationStoreModule.class)
public interface TestAvsVerificationStoreModule {

    @Binds
    VerificationStoreInitializer bindVerificationStoreInitializer(TestAvsVerificationStoreInitializer impl);
}
