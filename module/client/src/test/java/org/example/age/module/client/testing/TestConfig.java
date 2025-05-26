package org.example.age.module.client.testing;

import org.example.age.module.client.AvsClientsConfig;
import org.example.age.module.client.SiteClientsConfig;
import org.example.age.testing.TestClient;

/** Configuration for testing. */
public class TestConfig {

    /** Creates the configuration for clients on the site. */
    public static SiteClientsConfig createSiteClients(int port) {
        return SiteClientsConfig.builder()
                .avsUrl(TestClient.createLocalhostUrl(port))
                .build();
    }

    /** Creates the configuration for clients on the age verification service. */
    public static AvsClientsConfig createAvsClients(int port) {
        return AvsClientsConfig.builder()
                .putSiteUrls("site", TestClient.createLocalhostUrl(port))
                .build();
    }

    private TestConfig() {} // static class
}
