package org.example.age.demo.server;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.config.resource.ResourceAvsConfigModule;
import org.example.age.module.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.demo.DemoAccountIdExtractorModule;
import org.example.age.module.key.resource.ResourceAvsKeyModule;
import org.example.age.module.location.resource.ResourceSiteLocationModule;
import org.example.age.module.store.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.module.store.resource.ResourceVerificationStoreModule;
import org.example.age.service.endpoint.AvsServiceModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound {@code @Named("name") String}.</p>
 */
@Module(
        includes = {
            AvsServiceModule.class,
            DemoAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            ResourceVerificationStoreModule.class,
            InMemoryPendingStoreFactoryModule.class,
            ResourceAvsKeyModule.class,
            ResourceAvsConfigModule.class,
            ResourceSiteLocationModule.class,
            DemoResourceModule.class,
        })
interface DemoAvsServiceModule {}
