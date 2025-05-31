package org.example.age.service.module.request.testing;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.common.testing.TestClient;
import org.example.age.service.module.request.AccountIdContext;
import org.junit.jupiter.api.Test;

public abstract class AccountIdTestTemplate {

    @Test
    public void accountId() throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(TestClient.createLocalhostUrl(port()));
        setAccountId(requestBuilder, "username");
        Request request = requestBuilder.build();
        try (Response response = TestClient.getHttp().newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body().string()).isEqualTo("username");
        }
    }

    @Test
    public void noAccountId() throws IOException {
        Request request =
                new Request.Builder().url(TestClient.createLocalhostUrl(port())).build();
        try (Response response = TestClient.getHttp().newCall(request).execute()) {
            assertThat(response.code()).isEqualTo(401);
        }
    }

    protected abstract int port();

    protected abstract void setAccountId(Request.Builder requestBuilder, String accountId);

    /** Test service that responds with the account ID. */
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    public static final class TestService {

        private final AccountIdContext accountIdContext;

        public TestService(AccountIdContext accountIdContext) {
            this.accountIdContext = accountIdContext;
        }

        @GET
        public String accountId() {
            return accountIdContext.getForRequest();
        }
    }
}
