package org.example.age.service.endpoint.avs.test;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.config.avs.test.TestAvsConfigModule;
import org.example.age.module.extractor.common.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.common.test.TestAccountIdExtractorModule;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.module.location.common.test.TestSiteLocationModule;
import org.example.age.module.store.avs.test.TestAvsVerificationStoreModule;
import org.example.age.module.store.common.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.service.endpoint.avs.AvsServiceModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
@Module(
        includes = {
            AvsServiceModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            TestAvsVerificationStoreModule.class,
            InMemoryPendingStoreFactoryModule.class,
            TestKeyModule.class,
            TestAvsConfigModule.class,
            TestSiteLocationModule.class,
        })
interface TestAvsServiceModule {}
