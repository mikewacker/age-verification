package org.example.age.module.location.common.test;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.module.location.common.AvsLocation;
import org.example.age.module.location.common.RefreshableAvsLocationProvider;
import org.example.age.testing.server.TestServer;

@Singleton
final class TestAvsLocationProvider implements RefreshableAvsLocationProvider {

    @Inject
    public TestAvsLocationProvider() {}

    @Override
    public AvsLocation get() {
        TestServer<?> avsServer = TestServer.get("avs");
        return AvsLocation.builder(avsServer.host(), avsServer.port())
                .redirectPath("/verify")
                .build();
    }
}
