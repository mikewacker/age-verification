package org.example.age.demo.server;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.config.resource.ResourceSiteConfigModule;
import org.example.age.module.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.demo.DemoAccountIdExtractorModule;
import org.example.age.module.key.resource.ResourceSiteKeyModule;
import org.example.age.module.location.resource.ResourceAvsLocationModule;
import org.example.age.module.store.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.module.store.inmemory.InMemoryVerificationStoreModule;
import org.example.age.service.endpoint.SiteServiceModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound {@code @Named("name") String}.</p>
 */
@Module(
        includes = {
            SiteServiceModule.class,
            DemoAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            InMemoryVerificationStoreModule.class,
            InMemoryPendingStoreFactoryModule.class,
            ResourceSiteKeyModule.class,
            ResourceSiteConfigModule.class,
            ResourceAvsLocationModule.class,
            DemoResourceModule.class,
        })
interface DemoSiteServiceModule {}
