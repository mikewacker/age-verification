package org.example.age.service.component.stub;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.extractor.builtin.UserAgentAuthMatchDataExtractorModule;
import org.example.age.module.extractor.test.TestAccountIdExtractorModule;
import org.example.age.service.endpoint.StubSiteServiceModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
@Module(
        includes = {
            StubSiteServiceModule.class,
            TestAccountIdExtractorModule.class,
            UserAgentAuthMatchDataExtractorModule.class,
        })
interface TestStubSiteServiceModule {}
