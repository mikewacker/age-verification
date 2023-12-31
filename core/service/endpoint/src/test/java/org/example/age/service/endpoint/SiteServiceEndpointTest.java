package org.example.age.service.endpoint;

import io.github.mikewacker.drift.testing.server.TestServer;
import io.github.mikewacker.drift.testing.server.TestUndertowServer;
import java.io.IOException;
import org.example.age.service.component.fake.FakeAvsComponent;
import org.example.age.service.component.test.TestSiteComponent;
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
        ServiceEndpointTestTemplate.verify();
    }

    @Test
    public void verifyFailed_DuplicateVerification() throws IOException {
        ServiceEndpointTestTemplate.verifyFailed_DuplicateVerification();
    }
}
