package org.example.age.module.location.test;

import java.util.NoSuchElementException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.service.location.Location;
import org.example.age.service.location.RefreshableAvsLocationProvider;
import org.example.age.service.location.RefreshableSiteLocationProvider;
import org.example.age.testing.server.TestServer;

@Singleton
final class TestLocationProvider implements RefreshableAvsLocationProvider, RefreshableSiteLocationProvider {

    @Inject
    public TestLocationProvider() {}

    @Override
    public Location getAvs() {
        return getLocation("avs");
    }

    @Override
    public Location getSite(String siteId) {
        if (!siteId.equals("Site")) {
            throw new NoSuchElementException();
        }

        return getLocation("site");
    }

    /** Gets a {@link Location} from a {@link TestServer} with the specified name. */
    private static Location getLocation(String name) {
        TestServer<?> server = TestServer.get(name);
        return Location.of(server.host(), server.port());
    }
}
