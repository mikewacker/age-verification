package org.example.age.module.location.common.test;

import java.util.NoSuchElementException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.service.location.common.SiteLocation;
import org.example.age.service.module.location.common.RefreshableSiteLocationProvider;
import org.example.age.testing.server.TestServer;

@Singleton
final class TestSiteLocationProvider implements RefreshableSiteLocationProvider {

    @Inject
    public TestSiteLocationProvider() {}

    @Override
    public SiteLocation get(String siteId) {
        if (!siteId.equals("Site")) {
            throw new NoSuchElementException();
        }

        TestServer<?> siteServer = TestServer.get("site");
        return SiteLocation.builder(siteServer.host(), siteServer.port())
                .redirectPath("/verify")
                .build();
    }
}
