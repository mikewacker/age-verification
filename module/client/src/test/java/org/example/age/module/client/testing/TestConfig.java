package org.example.age.module.client.testing;

import java.net.URI;
import java.net.URL;
import org.example.age.module.client.AvsClientsConfig;
import org.example.age.module.client.SiteClientsConfig;

/** Configuration for testing. */
public class TestConfig {

    /** Creates the {@link SiteClientsConfig}. */
    public static SiteClientsConfig createSite(int port) {
        URL url = createLocalhostUrl(port);
        return SiteClientsConfig.builder().avsUrl(url).build();
    }

    /** Creates the {@link AvsClientsConfig}. */
    public static AvsClientsConfig createAvs(int port) {
        URL url = createLocalhostUrl(port);
        return AvsClientsConfig.builder().putSiteUrls("site", url).build();
    }

    /** Create a URL for localhost. */
    private static URL createLocalhostUrl(int port) {
        try {
            return new URI(String.format("http://localhost:%d", port)).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // static class
    private TestConfig() {}
}
