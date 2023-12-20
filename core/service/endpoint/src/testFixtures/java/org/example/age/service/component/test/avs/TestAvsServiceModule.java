package org.example.age.service.component.test.avs;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.config.test.avs.TestAvsConfigModule;
import org.example.age.module.extractor.builtin.common.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.test.common.TestAccountIdExtractorModule;
import org.example.age.module.key.test.common.TestKeyModule;
import org.example.age.module.location.test.common.TestSiteLocationModule;
import org.example.age.module.store.inmemory.common.InMemoryPendingStoreFactoryModule;
import org.example.age.module.store.test.avs.TestAvsVerificationStoreModule;
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
