package org.example.age.module.config.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import org.example.age.module.internal.resource.JsonResourceProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.service.config.AvsConfig;
import org.example.age.service.config.RefreshableAvsConfigProvider;

/** {@link RefreshableAvsConfigProvider} that gets config from a resource file. Is not refreshable. */
@Singleton
final class ResourceAvsConfigProvider extends JsonResourceProvider<AvsConfig> implements RefreshableAvsConfigProvider {

    @Inject
    public ResourceAvsConfigProvider(ResourceLoader resourceLoader, @Named("resources") Path rootPath) {
        super(resourceLoader, rootPath.resolve("config/config.json"), new TypeReference<>() {});
    }

    @Override
    public AvsConfig get() {
        return getInternal();
    }
}
