package org.example.age.module.client.testing;

import org.example.age.module.client.AvsClientsConfig;
import org.example.age.testing.client.TestClient;

/** Configuration for testing. */
public class TestConfig {

    private static AvsClientsConfig avsClients = AvsClientsConfig.builder()
            .putSiteUrls("site", TestClient.localhostUrl(8080))
            .build();

    /** Creates the configuration for clients on the age verification service. */
    public static AvsClientsConfig avsClients() {
        return avsClients;
    }

    private TestConfig() {} // static class
}
