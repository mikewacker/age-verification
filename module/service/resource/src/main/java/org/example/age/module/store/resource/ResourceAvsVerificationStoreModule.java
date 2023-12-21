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
 * which is an in-memory store without persistence.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("resources") {@link Class}&lt;?&gt;</code></li>
 *     <li><code>@Named("resources") {@link Path}</code></li>
 * </ul>
 */
@Module(includes = {InMemoryVerificationStoreModule.class, ResourceLoaderModule.class})
public interface ResourceAvsVerificationStoreModule {

    @Binds
    VerificationStoreInitializer bindVerificationStoreInitializer(ResourceAvsVerificationStoreInitializer impl);
}
