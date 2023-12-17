package org.example.age.service.endpoint.avs;

import java.io.IOException;
import org.example.age.service.endpoint.avs.test.TestAvsComponent;
import org.example.age.service.endpoint.site.fake.FakeSiteComponent;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.example.age.testing.service.ServiceIntegrationTestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsServiceEndpointTest {

    @RegisterExtension
    private static final TestServer<?> fakeSiteServer =
            TestUndertowServer.register("site", "/api/", FakeSiteComponent::createApiHandler);

    @RegisterExtension
    private static final TestServer<?> avsServer =
            TestUndertowServer.register("avs", "/api/", TestAvsComponent::createApiHandler);

    @Test
    public void verify() throws IOException {
        ServiceIntegrationTestTemplate.verify();
    }

    @Test
    public void verifyFailed_DuplicateVerification() throws IOException {
        ServiceIntegrationTestTemplate.verifyFailed_DuplicateVerification();
    }
}
