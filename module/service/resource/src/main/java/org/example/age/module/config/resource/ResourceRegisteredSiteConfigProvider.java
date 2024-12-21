package org.example.age.module.config.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.example.age.module.internal.resource.JsonResourceProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.service.config.RefreshableRegisteredSiteConfigProvider;
import org.example.age.service.config.RegisteredSiteConfig;

/** {@link RefreshableRegisteredSiteConfigProvider} that gets config from a resource file. Is not refreshable. */
@Singleton
final class ResourceRegisteredSiteConfigProvider extends JsonResourceProvider<Map<String, RegisteredSiteConfig>>
        implements RefreshableRegisteredSiteConfigProvider {

    @Inject
    public ResourceRegisteredSiteConfigProvider(ResourceLoader resourceLoader, @Named("resources") Path rootPath) {
        super(resourceLoader, rootPath.resolve("config/siteConfigs.json"), new TypeReference<>() {});
    }

    @Override
    public Optional<RegisteredSiteConfig> tryGet(String siteId) {
        Map<String, RegisteredSiteConfig> siteConfigs = getInternal();
        return Optional.ofNullable(siteConfigs.get(siteId));
    }
}
