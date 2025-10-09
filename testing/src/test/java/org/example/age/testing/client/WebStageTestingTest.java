package org.example.age.testing.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;

public final class WebStageTestingTest {

    @Test
    public void await_Success() {
        CompletionStage<String> stage = CompletableFuture.completedStage("value");
        String value = await(stage);
        assertThat(value).isEqualTo("value");
    }

    @Test
    public void await_ErrorCode() {
        CompletionStage<?> stage = CompletableFuture.failedStage(new NotFoundException());
        assertThatThrownBy(() -> await(stage))
                .isInstanceOf(AssertionError.class)
                .hasCauseInstanceOf(NotFoundException.class)
                .hasMessage("404 error");
    }

    @Test
    public void await_Exception() {
        CompletionStage<?> stage = CompletableFuture.failedStage(new IllegalStateException());
        assertThatThrownBy(() -> await(stage))
                .isInstanceOf(AssertionError.class)
                .hasCauseInstanceOf(IllegalStateException.class)
                .hasMessage("500 error");
    }

    @Test
    public void await_Timeout() {
        CompletionStage<?> stage = CompletableFuture.supplyAsync(WebStageTestingTest::sleep);
        assertThatThrownBy(() -> await(stage, Duration.ofMillis(1)))
                .isInstanceOf(AssertionError.class)
                .hasCauseInstanceOf(TimeoutException.class)
                .hasMessage("");
    }

    @Test
    public void awaitErrorCode_Success() {
        CompletionStage<?> stage = CompletableFuture.completedStage("value");
        assertThatThrownBy(() -> awaitErrorCode(stage, 404))
                .isInstanceOf(AssertionError.class)
                .hasMessage("stage succeeded, expected a 404 error");
    }

    @Test
    public void awaitErrorCode_ExpectedErrorCode() {
        CompletionStage<?> stage = CompletableFuture.failedStage(new NotFoundException());
        awaitErrorCode(stage, 404);
    }

    @Test
    public void awaitErrorCode_UnexpectedErrorCode() {
        CompletionStage<?> stage = CompletableFuture.failedStage(new ForbiddenException());
        assertThatThrownBy(() -> awaitErrorCode(stage, 404))
                .isInstanceOf(AssertionError.class)
                .hasCauseInstanceOf(ForbiddenException.class)
                .hasMessage("stage failed with a 403 error, expected a 404 error");
    }

    @Test
    public void awaitErrorCode_Exception() {
        CompletionStage<?> stage = CompletableFuture.failedStage(new IllegalStateException());
        assertThatThrownBy(() -> awaitErrorCode(stage, 404))
                .isInstanceOf(AssertionError.class)
                .hasCauseInstanceOf(IllegalStateException.class)
                .hasMessage("stage failed with a 500 error, expected a 404 error");
    }

    @Test
    public void awaitErrorCode_Timeout() {
        CompletionStage<?> stage = CompletableFuture.supplyAsync(WebStageTestingTest::sleep);
        assertThatThrownBy(() -> awaitErrorCode(stage, 404, Duration.ofMillis(1)))
                .isInstanceOf(AssertionError.class)
                .hasCauseInstanceOf(TimeoutException.class)
                .hasMessage("");
    }

    private static Void sleep() {
        try {
            Thread.sleep(3);
            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
