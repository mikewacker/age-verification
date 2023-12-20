package org.example.age.service.endpoint.avs;

import java.io.IOException;
import org.example.age.service.component.fake.site.FakeSiteComponent;
import org.example.age.service.component.test.avs.TestAvsComponent;
import org.example.age.service.endpoint.ServiceEndpointTestTemplate;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
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
