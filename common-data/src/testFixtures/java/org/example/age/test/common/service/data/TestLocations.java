package org.example.age.test.common.service.data;

import org.example.age.common.service.data.AvsLocation;
import org.example.age.common.service.data.SiteLocation;
import org.example.age.testing.server.TestServer;

/** Gets locations from {@link TestServer}'s. */
final class TestLocations {

    /** Gets the {@link AvsLocation} from the corresponding {@link TestServer}. */
    public static AvsLocation avs(TestServer<?> avsServer) {
        return AvsLocation.builder(avsServer.hostAndPort())
                .redirectPath("verify")
                .build();
    }

    /** Gets the {@link SiteLocation} from the corresponding {@link TestServer}. */
    public static SiteLocation site(TestServer<?> siteServer) {
        return SiteLocation.builder(siteServer.hostAndPort())
                .redirectPath("verify")
                .build();
    }

    // static class
    private TestLocations() {}
}
