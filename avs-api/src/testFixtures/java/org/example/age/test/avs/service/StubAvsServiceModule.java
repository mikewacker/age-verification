package org.example.age.test.avs.service;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.api.AvsApiModule;
import org.example.age.common.service.data.UserAgentAuthMatchDataExtractorModule;
import org.example.age.common.service.data.internal.DataMapperModule;
import org.example.age.test.common.service.data.TestAccountIdExtractorModule;

/** Dagger module that binds dependencies for <code>@Named("api") HttpHandler</code>. */
@Module(
        includes = {
            AvsApiModule.class,
            TestAccountIdExtractorModule.class,
            UserAgentAuthMatchDataExtractorModule.class,
            DataMapperModule.class,
        })
public interface StubAvsServiceModule {

    @Binds
    AvsApi bindAvsApi(StubAvsService service);
}
