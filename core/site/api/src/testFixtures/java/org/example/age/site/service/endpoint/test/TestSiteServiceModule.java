package org.example.age.site.service.endpoint.test;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.common.api.extractor.builtin.UserAgentAuthMatchDataExtractorModule;
import org.example.age.common.api.extractor.test.TestAccountIdExtractorModule;
import org.example.age.site.service.endpoint.StubSiteServiceModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
@Module(
        includes = {
            StubSiteServiceModule.class,
            TestAccountIdExtractorModule.class,
            UserAgentAuthMatchDataExtractorModule.class,
        })
public interface TestSiteServiceModule {}