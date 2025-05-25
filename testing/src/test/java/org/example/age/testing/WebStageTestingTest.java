package org.example.age.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.age.testing.WebStageTesting.await;
import static org.example.age.testing.WebStageTesting.awaitErrorCode;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

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

    @Test
    public void toCall_Success() throws IOException {
        CompletionStage<String> stage = CompletableFuture.completedStage("value");
        Response<String> response = WebStageTesting.toCall(stage).execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body()).isEqualTo("value");
    }

    @Test
    public void toCall_ErrorCode() throws IOException {
        CompletionStage<?> stage = CompletableFuture.failedStage(new NotFoundException());
        Response<?> response = WebStageTesting.toCall(stage).execute();
        assertThat(response.code()).isEqualTo(404);
    }

    @Test
    public void toCall_Exception() throws IOException {
        CompletionStage<?> stage = CompletableFuture.failedStage(new IllegalStateException());
        Response<?> response = WebStageTesting.toCall(stage).execute();
        assertThat(response.code()).isEqualTo(500);
    }

    @Test
    public void toCall_Timeout() {
        CompletionStage<?> stage = CompletableFuture.supplyAsync(WebStageTestingTest::sleep);
        Call<?> call = WebStageTesting.toCall(stage, Duration.ofMillis(1));
        assertThatThrownBy(call::execute).isInstanceOf(TimeoutException.class);
    }

    @Test
    public void wrapExceptions() {
        CompletionStage<?> stage = WebStageTesting.wrapExceptions(() -> {
            throw new NotFoundException();
        });
        awaitErrorCode(stage, 404);
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
