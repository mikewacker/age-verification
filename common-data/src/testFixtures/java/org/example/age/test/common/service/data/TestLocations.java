package org.example.age.test.common.service.data;

import org.example.age.common.service.data.AvsLocation;
import org.example.age.common.service.data.SiteLocation;
import org.example.age.testing.server.TestServer;

/** Test {@link AvsLocation}'s and {@link SiteLocation}'s. */
final class TestLocations {

    private static final String STUB_HOST = "localhost";
    private static final int STUB_PORT = 80;
    private static final String REDIRECT_PATH = "/verify";

    /** Gets the {@link AvsLocation} from the corresponding {@link TestServer}. */
    public static AvsLocation avs(TestServer<?> avsServer) {
        return avs(avsServer.host(), avsServer.port());
    }

    /** Gets the {@link SiteLocation} from the corresponding {@link TestServer}. */
    public static SiteLocation site(TestServer<?> siteServer) {
        return site(siteServer.host(), siteServer.port());
    }

    /** Gets a stub {@link AvsLocation}. */
    public static AvsLocation stubAvs() {
        return avs(STUB_HOST, STUB_PORT);
    }

    /** Gets a stub {@link SiteLocation}. */
    public static SiteLocation stubSite() {
        return site(STUB_HOST, STUB_PORT);
    }

    /** Gets a {@link AvsLocation}. */
    private static AvsLocation avs(String host, int port) {
        return AvsLocation.builder(host, port).redirectPath(REDIRECT_PATH).build();
    }

    /** Gets a {@link SiteLocation}. */
    private static SiteLocation site(String host, int port) {
        return SiteLocation.builder(host, port).redirectPath(REDIRECT_PATH).build();
    }

    // static class
    private TestLocations() {}
}
