package org.example.age.service.avs.endpoint.test;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.module.config.common.test.TestSiteLocationModule;
import org.example.age.module.extractor.common.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.module.extractor.common.test.TestAccountIdExtractorModule;
import org.example.age.module.key.common.test.TestKeyModule;
import org.example.age.service.avs.endpoint.FakeAvsServiceModule;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound <code>@Named("site") {@link TestServer}&lt?&gt;</code>.</p>
 */
@Module(
        includes = {
            FakeAvsServiceModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            TestKeyModule.class,
            TestSiteLocationModule.class,
        })
public interface TestFakeAvsServiceModule {}
