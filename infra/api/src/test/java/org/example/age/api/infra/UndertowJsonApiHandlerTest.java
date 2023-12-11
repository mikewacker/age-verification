package org.example.age.api.infra;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.ValueSender;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowJsonApiHandlerTest {

    @RegisterExtension
    private static final TestServer<?> server =
            TestUndertowServer.register("test", UndertowJsonApiHandlerTest::createAddHandler);

    @Test
    public void exchange() throws IOException {
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

    private static HttpOptional<Integer> executeAddRequest(String path, Object bodyOperand) throws IOException {
        return TestClient.apiRequestBuilder()
                .post(server.url(path))
                .body(bodyOperand)
                .executeWithJsonResponse(new TypeReference<>() {});
    }

    /** Creates an {@link HttpHandler} that adds two operands in the request body and in a query parameter. */
    private static HttpHandler createAddHandler() {
        return UndertowJsonApiHandler.builder(new TypeReference<Integer>() {})
                .addBody(new TypeReference<Integer>() {})
                .addQueryParam("operand", new TypeReference<Integer>() {})
                .build(UndertowJsonApiHandlerTest::add);
    }

    /** Adds two numbers, throwing an exception if the sum is 500. */
    private static void add(ValueSender<Integer> sender, int operand1, int operand2, Dispatcher dispatcher) {
        int sum = operand1 + operand2;
        if (sum == 500) {
            throw new RuntimeException();
        }

        sender.sendValue(sum);
    }
}
