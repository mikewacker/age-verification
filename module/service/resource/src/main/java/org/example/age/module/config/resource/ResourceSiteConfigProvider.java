package org.example.age.module.config.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.module.internal.resource.JsonResourceProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.service.config.RefreshableSiteConfigProvider;
import org.example.age.service.config.SiteConfig;

/** {@link RefreshableSiteConfigProvider} that gets config from a resource file. Is not refreshable. */
@Singleton
final class ResourceSiteConfigProvider extends JsonResourceProvider<SiteConfig>
        implements RefreshableSiteConfigProvider {

    @Inject
    public ResourceSiteConfigProvider(ResourceLoader resourceLoader, @Named("resources") Path rootPath) {
        super(resourceLoader, rootPath.resolve("config/config.json"), new TypeReference<>() {});
    }

    @Override
    public SiteConfig get() {
        return getInternal();
    }
}
