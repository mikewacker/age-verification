package org.example.age.testing.app;

import java.net.URL;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.site.api.client.SiteApi;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.integration.VerificationTestTemplate;

public final class AppVerificationTest extends VerificationTestTemplate {

    private static final SiteApi siteClient = createClient("site", "username", SiteApi.class);
    private static final AvsApi avsClient = createClient("avs", "person", AvsApi.class);

    @Override
    protected SiteApi siteClient() {
        return siteClient;
    }

    @Override
    protected AvsApi avsClient() {
        return avsClient;
    }

    private static <A> A createClient(String service, String accountId, Class<A> apiType) {
        URL url = TestClient.dockerUrl(service, 80);
        return TestClient.api(url, requestBuilder -> requestBuilder.header("Account-Id", accountId), apiType);
    }
}
