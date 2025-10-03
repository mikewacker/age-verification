package org.example.age.testing.common.spi;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.Test;

public abstract class AccountIdTestTemplate {

    @Test
    public void accountId() throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(TestClient.localhostUrl(port()));
        setAccountId(requestBuilder, "username");
        Request request = requestBuilder.build();
        try (Response response = TestClient.http().newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body().string()).isEqualTo("username");
        }
    }

    @Test
    public void noAccountId() throws IOException {
        Request request =
                new Request.Builder().url(TestClient.localhostUrl(port())).build();
        try (Response response = TestClient.http().newCall(request).execute()) {
            assertThat(response.code()).isEqualTo(401);
        }
    }

    protected abstract void setAccountId(Request.Builder requestBuilder, String accountId);

    protected abstract int port();

    /** Test endpoint that responds with the account ID. */
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    public record TestEndpoint(AccountIdContext accountIdContext) {

        @GET
        public String accountId() {
            return accountIdContext.getForRequest();
        }
    }
}
