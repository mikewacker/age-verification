package org.example.age.api.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.StatusCodeSender;
import org.example.age.api.base.ValueSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowJsonApiHandlerTest {

    @RegisterExtension
    private static final TestServer<?> okServer =
            TestUndertowServer.register("ok", UndertowJsonApiHandlerTest::createOkHandler);

    @RegisterExtension
    private static final TestServer<?> addServer =
            TestUndertowServer.register("add", UndertowJsonApiHandlerTest::createAddHandler);

    @Test
    public void exchange_Ok() throws IOException {
        int statusCode = executeOkRequest();
        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void exchange_JsonValue() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=2", 2);
        assertThat(maybeSum).hasValue(4);
    }

    @Test
    public void error_MissingParam() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add", 2);
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_InvalidParam() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=a", 2);
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_InvalidBody() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=2", "a");
        assertThat(maybeSum).isEmptyWithErrorCode(400);
    }

    @Test
    public void error_UncaughtExceptionInCallback() throws IOException {
        HttpOptional<Integer> maybeSum = executeAddRequest("/add?operand=200", 300);
        assertThat(maybeSum).isEmptyWithErrorCode(500);
    }

    private static int executeOkRequest() throws IOException {
        return TestClient.requestBuilder().get(okServer.rootUrl()).execute();
    }

    private static HttpOptional<Integer> executeAddRequest(String path, Object bodyOperand) throws IOException {
        return TestClient.requestBuilder(new TypeReference<Integer>() {})
                .post(addServer.url(path))
                .body(bodyOperand)
                .execute();
    }

    /** Creates an {@link HttpHandler} that sends a 200 status code. */
    private static HttpHandler createOkHandler() {
        return UndertowJsonApiHandler.builder().build(UndertowJsonApiHandlerTest::ok);
    }

    private static void ok(StatusCodeSender sender, Dispatcher dispatcher) {
        sender.sendOk();
    }

    /**
     * Creates an {@link HttpHandler} that adds two numbers: one in the request body, and one in a query parameter.
     *
     * <p>The handler throw an exception if the sum is 500.</p>
     */
    private static HttpHandler createAddHandler() {
        return UndertowJsonApiHandler.builder(new TypeReference<Integer>() {})
                .addBody(new TypeReference<Integer>() {})
                .addQueryParam("operand", new TypeReference<Integer>() {})
                .build(UndertowJsonApiHandlerTest::add);
    }

    private static void add(ValueSender<Integer> sender, int operand1, int operand2, Dispatcher dispatcher) {
        int sum = operand1 + operand2;
        if (sum == 500) {
            throw new RuntimeException();
        }

        sender.sendValue(sum);
    }
}
