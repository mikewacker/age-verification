package org.example.age.service.component.test;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.config.test.TestSiteConfigModule;
import org.example.age.module.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.test.TestAccountIdExtractorModule;
import org.example.age.module.key.test.TestKeyModule;
import org.example.age.module.location.test.TestAvsLocationModule;
import org.example.age.module.store.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.module.store.inmemory.InMemoryVerificationStoreModule;
import org.example.age.service.endpoint.SiteServiceModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
@Module(
        includes = {
            SiteServiceModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            InMemoryVerificationStoreModule.class,
            InMemoryPendingStoreFactoryModule.class,
            TestKeyModule.class,
            TestSiteConfigModule.class,
            TestAvsLocationModule.class,
        })
interface TestSiteServiceModule {}
