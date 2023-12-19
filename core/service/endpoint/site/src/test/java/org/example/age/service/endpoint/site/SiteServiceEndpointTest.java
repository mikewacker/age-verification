package org.example.age.service.endpoint.site;

import java.io.IOException;
import org.example.age.service.component.fake.avs.FakeAvsComponent;
import org.example.age.service.component.test.site.TestSiteComponent;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.example.age.testing.service.ServiceIntegrationTestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteServiceEndpointTest {

    @RegisterExtension
    private static final TestServer<?> siteServer =
            TestUndertowServer.register("site", "/api/", TestSiteComponent::createApiHandler);

    @RegisterExtension
    private static final TestServer<?> fakeAvsServer =
            TestUndertowServer.register("avs", "/api/", FakeAvsComponent::createApiHandler);

    @Test
    public void verify() throws IOException {
        ServiceIntegrationTestTemplate.verify();
    }

    @Test
    public void verifyFailed_DuplicateVerification() throws IOException {
        ServiceIntegrationTestTemplate.verifyFailed_DuplicateVerification();
    }
}
