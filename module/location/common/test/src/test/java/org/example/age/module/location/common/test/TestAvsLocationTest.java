package org.example.age.module.location.common.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.io.IOException;
import javax.inject.Singleton;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.module.location.common.RefreshableAvsLocationProvider;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.mock.MockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestAvsLocationTest {

    @RegisterExtension
    private static final MockServer avsServer = MockServer.register("avs");

    private static RefreshableAvsLocationProvider avsLocationProvider;

    @BeforeAll
    public static void createRefreshableAvsLocationProvider() {
        avsLocationProvider = TestComponent.createRefreshableAvsLocationProvider();
    }

    @Test
    public void exchange() throws IOException {
        avsServer.enqueue(new MockResponse());
        String avsUrl = avsLocationProvider.get().verificationSessionUrl("Site");
        int statusCode = TestClient.requestBuilder().get(avsUrl).execute();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void exchange_DifferentLocation() throws IOException {
        exchange();
    }

    @Component(modules = TestAvsLocationModule.class)
    @Singleton
    interface TestComponent {

        static RefreshableAvsLocationProvider createRefreshableAvsLocationProvider() {
            TestComponent component = DaggerTestAvsLocationTest_TestComponent.create();
            return component.refreshableAvsLocationProvider();
        }

        RefreshableAvsLocationProvider refreshableAvsLocationProvider();
    }
}
