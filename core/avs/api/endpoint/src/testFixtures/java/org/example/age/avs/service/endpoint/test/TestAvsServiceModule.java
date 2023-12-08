package org.example.age.avs.service.endpoint.test;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.avs.service.endpoint.StubAvsServiceModule;
import org.example.age.module.extractor.common.builtin.UserAgentAuthMatchDataExtractorModule;
import org.example.age.module.extractor.common.test.TestAccountIdExtractorModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
@Module(
        includes = {
            StubAvsServiceModule.class,
            TestAccountIdExtractorModule.class,
            UserAgentAuthMatchDataExtractorModule.class,
        })
public interface TestAvsServiceModule {}
