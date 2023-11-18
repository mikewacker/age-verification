package org.example.age.site.api.test;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import org.example.age.common.service.data.DataMapperModule;
import org.example.age.common.service.data.UserAgentAuthMatchDataExtractorModule;
import org.example.age.site.api.SiteApi;
import org.example.age.site.api.SiteApiModule;
import org.example.age.test.service.data.TestAccountIdExtractorModule;

/** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
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
