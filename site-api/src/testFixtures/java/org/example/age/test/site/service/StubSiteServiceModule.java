package org.example.age.test.site.service;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.extractor.builtin.UserAgentAuthMatchDataExtractorModule;
import org.example.age.common.api.extractor.test.TestAccountIdExtractorModule;
import org.example.age.site.api.SiteApi;
import org.example.age.site.api.SiteApiModule;

/** Dagger module that binds dependencies for <code>@Named("api") HttpHandler</code>. */
@Module(
        includes = {
            SiteApiModule.class,
            TestAccountIdExtractorModule.class,
            UserAgentAuthMatchDataExtractorModule.class,
        })
public interface StubSiteServiceModule {

    @Binds
    SiteApi bindAvsApi(StubSiteService service);
}
