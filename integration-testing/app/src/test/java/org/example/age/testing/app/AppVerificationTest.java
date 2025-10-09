package org.example.age.testing.app;

import org.example.age.avs.api.client.AvsApi;
import org.example.age.site.api.client.SiteApi;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.integration.VerificationTestTemplate;

public final class AppVerificationTest extends VerificationTestTemplate {

    private static final SiteApi siteClient = createClient(8080, "username", SiteApi.class);
    private static final AvsApi avsClient = createClient(9090, "person", AvsApi.class);

    @Override
    protected SiteApi siteClient() {
        return siteClient;
    }

    @Override
    protected AvsApi avsClient() {
        return avsClient;
    }

    private static <A> A createClient(int port, String accountId, Class<A> apiType) {
        return TestClient.api(port, requestBuilder -> requestBuilder.header("Account-Id", accountId), apiType);
    }
}
