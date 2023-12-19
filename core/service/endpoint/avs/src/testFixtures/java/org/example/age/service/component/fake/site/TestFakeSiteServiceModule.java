package org.example.age.service.component.fake.site;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.extractor.common.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.common.test.TestAccountIdExtractorModule;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.module.location.common.test.TestAvsLocationModule;
import org.example.age.module.store.common.inmemory.InMemoryVerificationStoreModule;
import org.example.age.service.endpoint.site.FakeSiteServiceModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
@Module(
        includes = {
            FakeSiteServiceModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            InMemoryVerificationStoreModule.class,
            TestKeyModule.class,
            TestAvsLocationModule.class,
        })
interface TestFakeSiteServiceModule {}
