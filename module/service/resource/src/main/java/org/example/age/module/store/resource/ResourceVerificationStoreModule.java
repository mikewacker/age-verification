package org.example.age.module.store.resource;

import dagger.Binds;
import dagger.Module;
import java.nio.file.Path;
import org.example.age.module.internal.resource.ResourceLoaderModule;
import org.example.age.module.store.inmemory.InMemoryVerificationStoreModule;
import org.example.age.module.store.inmemory.VerificationStoreInitializer;
import org.example.age.service.store.VerificationStore;

/**
 * Dagger module that binds dependencies for {@link VerificationStore},
 * which is an in-memory store without persistence, loading accounts from {@code store/accounts.json}.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("resources") {@link Class}&lt;?&gt;</code></li>
 *     <li><code>@Named("resources") {@link Path}</code></li>
 * </ul>
 *
 * <p>It loads resources before the server starts and is not refreshable.</p>
 */
@Module(includes = {InMemoryVerificationStoreModule.class, ResourceLoaderModule.class})
public interface ResourceVerificationStoreModule {

    @Binds
    VerificationStoreInitializer bindVerificationStoreInitializer(ResourceVerificationStoreInitializer impl);
}
