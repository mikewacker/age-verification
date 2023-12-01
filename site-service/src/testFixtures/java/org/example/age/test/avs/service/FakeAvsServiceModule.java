package org.example.age.test.avs.service;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.api.AvsApiModule;
import org.example.age.common.api.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.common.api.extractor.test.TestAccountIdExtractorModule;
import org.example.age.common.service.data.internal.DataMapperModule;
import org.example.age.infra.service.client.RequestDispatcherModule;
import org.example.age.test.avs.service.verification.internal.FakeAvsVerificationFactoryModule;
import org.example.age.test.common.service.data.TestSiteLocationModule;
import org.example.age.testing.server.TestServer;

/**
 * Dagger module that binds dependencies for <code>@Named("api") HttpHandler</code>.
 *
 * <p>Depends on an unbound <code>{@link TestServer}&lt?&gt;</code>.</p>
 */
@Module(
        includes = {
            AvsApiModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            FakeAvsVerificationFactoryModule.class,
            RequestDispatcherModule.class,
            TestSiteLocationModule.class,
            DataMapperModule.class,
        })
public interface FakeAvsServiceModule {

    @Binds
    AvsApi bindAvsApi(FakeAvsService service);
}
