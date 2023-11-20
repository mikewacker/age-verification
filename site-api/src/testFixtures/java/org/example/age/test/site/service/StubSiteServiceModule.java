package org.example.age.test.site.service;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.service.data.UserAgentAuthMatchDataExtractorModule;
import org.example.age.common.service.data.internal.DataMapperModule;
import org.example.age.site.api.SiteApi;
import org.example.age.site.api.SiteApiModule;
import org.example.age.test.common.service.data.TestAccountIdExtractorModule;

/** Dagger module that binds dependencies for <code>@Named("api") HttpHandler</code>. */
@Module(
        includes = {
            SiteApiModule.class,
            TestAccountIdExtractorModule.class,
            UserAgentAuthMatchDataExtractorModule.class,
            DataMapperModule.class,
        })
public interface StubSiteServiceModule {

    @Binds
    SiteApi bindAvsApi(StubSiteService service);
}
