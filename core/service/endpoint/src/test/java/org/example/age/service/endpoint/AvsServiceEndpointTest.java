package org.example.age.service.endpoint;

import io.github.mikewacker.drift.testing.server.TestServer;
import io.github.mikewacker.drift.testing.server.TestUndertowServer;
import java.io.IOException;
import org.example.age.service.component.fake.FakeSiteComponent;
import org.example.age.service.component.test.TestAvsComponent;
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
        ServiceEndpointTestTemplate.verify();
    }

    @Test
    public void verifyFailed_DuplicateVerification() throws IOException {
        ServiceEndpointTestTemplate.verifyFailed_DuplicateVerification();
    }
}
