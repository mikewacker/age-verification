package org.example.age.service.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import jakarta.ws.rs.WebApplicationException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/** Assertions for testing services. */
public final class Assertions {

    /** Asserts that a response is unsuccessful with the expected error code. */
    public static void assertErrorCodeIs(CompletionStage<?> response, int expectedErrorCode) {
        assertThat(response).isCompletedExceptionally();
        try {
            response.toCompletableFuture().get();
            fail();
        } catch (ExecutionException execE) {
            assertThat(execE.getCause()).isInstanceOf(WebApplicationException.class);
            WebApplicationException e = (WebApplicationException) execE.getCause();
            assertThat(e.getResponse().getStatus()).isEqualTo(expectedErrorCode);
        } catch (Exception e) {
            fail();
        }
    }

    // static class
    private Assertions() {}
}
