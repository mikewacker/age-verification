package org.example.age.avs.service.endpoint.test;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.avs.service.endpoint.FakeAvsServiceModule;
import org.example.age.common.api.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.common.api.extractor.test.TestAccountIdExtractorModule;
import org.example.age.common.service.config.test.TestSiteLocationModule;
import org.example.age.common.service.key.test.TestKeyModule;
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
public interface TestAvsServiceModule {}
