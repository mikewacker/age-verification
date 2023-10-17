package org.example.age.common.avs.api;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import io.undertow.Undertow;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.common.testing.TestUndertowModule;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsApiHttpHandlerTest {

    @RegisterExtension
    private static final TestUndertowServer avsServer = TestUndertowServer.create(TestComponent::createServer);

    @Test
    public void stubTest() throws IOException {
        String url = avsServer.url("/api/example");
        Request request = createPostRequest(url);
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(404);
    }

    private static Request createPostRequest(String url) {
        RequestBody emptyBody = RequestBody.create(new byte[0]);
        return new Request.Builder().url(url).post(emptyBody).build();
    }

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = {TestUndertowModule.class, AvsApiModule.class})
    @Singleton
    interface TestComponent {

        static Undertow createServer(int port) {
            TestComponent component =
                    DaggerAvsApiHttpHandlerTest_TestComponent.factory().create(port);
            return component.server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
