package org.example.age.service.component.fake;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.test.TestAccountIdExtractorModule;
import org.example.age.module.key.test.TestKeyModule;
import org.example.age.module.location.test.TestSiteLocationModule;
import org.example.age.module.store.test.TestAvsVerificationStoreModule;
import org.example.age.service.endpoint.FakeAvsServiceModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
@Module(
        includes = {
            FakeAvsServiceModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            TestAvsVerificationStoreModule.class,
            TestKeyModule.class,
            TestSiteLocationModule.class,
        })
interface TestFakeAvsServiceModule {}
