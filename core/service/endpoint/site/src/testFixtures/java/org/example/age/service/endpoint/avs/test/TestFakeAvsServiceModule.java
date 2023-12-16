package org.example.age.service.endpoint.avs.test;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.extractor.common.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.common.test.TestAccountIdExtractorModule;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.module.location.common.test.TestSiteLocationModule;
import org.example.age.service.avs.endpoint.FakeAvsServiceModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
@Module(
        includes = {
            FakeAvsServiceModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            TestKeyModule.class,
            TestSiteLocationModule.class,
        })
interface TestFakeAvsServiceModule {}
