package org.example.age.module.key.resource;

import dagger.Binds;
import dagger.Module;
import java.nio.file.Path;
import org.example.age.module.internal.resource.ResourceLoaderModule;
import org.example.age.service.key.RefreshablePrivateSigningKeyProvider;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li>{@link RefreshablePrivateSigningKeyProvider}, which loads the key from {@code key/privateSigningKey.pem}</li>
 *     <li>{@link RefreshablePseudonymKeyProvider}, which loads the keys from {@code key/pseudonymKeys.json}</li>
 * </ul>
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li><code>@Named("resources") {@link Class}&lt;?&gt;</code></li>
 *     <li><code>@Named("resources") {@link Path}</code></li>
 * </ul>
 *
 * <p>It loads resources before the server starts and is not refreshable.</p>
 */
@Module(includes = ResourceLoaderModule.class)
public interface ResourceAvsKeyModule {

    @Binds
    RefreshablePrivateSigningKeyProvider bindRefreshablePrivateSigningKeyProvider(
            ResourcePrivateSigningKeyProvider impl);

    @Binds
    RefreshablePseudonymKeyProvider bindRefreshablePseudonymKeyProvider(ResourcePseudonymKeyProvider impl);
}
