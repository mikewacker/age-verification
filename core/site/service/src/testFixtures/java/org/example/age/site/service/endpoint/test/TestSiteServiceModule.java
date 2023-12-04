package org.example.age.site.service.endpoint.test;

import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.common.api.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.common.api.extractor.test.TestAccountIdExtractorModule;
import org.example.age.common.service.key.test.TestKeyModule;
import org.example.age.common.service.store.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.site.service.config.test.TestSiteConfigModule;
import org.example.age.site.service.endpoint.SiteServiceModule;
import org.example.age.site.service.store.InMemoryVerificationStoreModule;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound <code>@Named("avs") {@link TestServer}&lt?&gt;</code>.</p>
 */
@Module(
        includes = {
            SiteServiceModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            InMemoryVerificationStoreModule.class,
            InMemoryPendingStoreFactoryModule.class,
            TestKeyModule.class,
            TestSiteConfigModule.class,
        })
public class TestSiteServiceModule {}
