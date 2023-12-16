package org.example.age.service.endpoint.site.stub;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.extractor.common.builtin.UserAgentAuthMatchDataExtractorModule;
import org.example.age.module.extractor.common.test.TestAccountIdExtractorModule;
import org.example.age.service.endpoint.site.StubSiteServiceModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
@Module(
        includes = {
            StubSiteServiceModule.class,
            TestAccountIdExtractorModule.class,
            UserAgentAuthMatchDataExtractorModule.class,
        })
interface TestStubSiteServiceModule {}
