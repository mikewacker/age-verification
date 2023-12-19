package org.example.age.service.component.fake.site;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.extractor.builtin.common.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.test.common.TestAccountIdExtractorModule;
import org.example.age.module.key.test.common.TestKeyModule;
import org.example.age.module.location.test.common.TestAvsLocationModule;
import org.example.age.module.store.inmemory.common.InMemoryVerificationStoreModule;
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
