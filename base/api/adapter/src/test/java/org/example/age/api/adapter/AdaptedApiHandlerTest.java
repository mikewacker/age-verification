package org.example.age.api.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;
import org.example.age.testing.api.FakeSender;
import org.example.age.testing.api.StubDispatcher;
import org.junit.jupiter.api.Test;

public final class AdaptedApiHandlerTest {

    @Test
    public void handleRequest_ZeroArg() throws Exception {
        AdaptedApiHandler<TestExchange> handler = AdaptedApiHandler.builder(
                        TestExchange::sender, TestExchange::dispatcher)
                .build(AdaptedApiHandlerTest::add0);
        handleRequest(handler, TestExchange.of(null, null, null, null), 0);
    }

    @Test
    public void handleRequest_OneArg() throws Exception {
        AdaptedApiHandler<TestExchange> handler = AdaptedApiHandler.builder(
                        TestExchange::sender, TestExchange::dispatcher)
                .addExtractor(TestExchange::extractOperand1)
                .build(AdaptedApiHandlerTest::add1);
        handleRequest(handler, TestExchange.of("1", null, null, null), 1);
    }

    @Test
    public void handleRequest_TwoArg() throws Exception {
        AdaptedApiHandler<TestExchange> handler = AdaptedApiHandler.builder(
                        TestExchange::sender, TestExchange::dispatcher)
                .addExtractor(TestExchange::extractOperand1)
                .addExtractor(TestExchange::extractOperand2)
                .build(AdaptedApiHandlerTest::add2);
        handleRequest(handler, TestExchange.of("1", "2", null, null), 3);
    }

    @Test
    public void handleRequest_ThreeArg() throws Exception {
        AdaptedApiHandler<TestExchange> handler = AdaptedApiHandler.builder(
                        TestExchange::sender, TestExchange::dispatcher)
                .addExtractor(TestExchange::extractOperand1)
                .addExtractor(TestExchange::extractOperand2)
                .addExtractor(TestExchange::extractOperand3)
                .build(AdaptedApiHandlerTest::add3);
        handleRequest(handler, TestExchange.of("1", "2", "3", null), 6);
    }

    @Test
    public void handleRequest_FourArg() throws Exception {
        AdaptedApiHandler<TestExchange> handler = AdaptedApiHandler.builder(
                        TestExchange::sender, TestExchange::dispatcher)
                .addExtractor(TestExchange::extractOperand1)
                .addExtractor(TestExchange::extractOperand2)
                .addExtractor(TestExchange::extractOperand3)
                .addExtractor(TestExchange::extractOperand4)
                .build(AdaptedApiHandlerTest::add4);
        handleRequest(handler, TestExchange.of("1", "2", "3", "4"), 10);
    }

    private void handleRequest(AdaptedApiHandler<TestExchange> handler, TestExchange exchange, int expectedSum)
            throws Exception {
        handler.handleRequest(exchange);
        assertThat(exchange.sender().tryGet()).hasValue(HttpOptional.of(expectedSum));
    }

    @Test
    public void error_HandleRequest_ExtractorFailed() throws Exception {
        AdaptedApiHandler<TestExchange> handler = AdaptedApiHandler.builder(
                        TestExchange::sender, TestExchange::dispatcher)
                .addExtractor(TestExchange::extractOperand1)
                .build(AdaptedApiHandlerTest::add1);
        TestExchange exchange = TestExchange.of("a", null, null, null);
        handler.handleRequest(exchange);
        assertThat(exchange.sender().tryGet()).hasValue(HttpOptional.empty(400));
    }

    private static void add0(Sender.Value<Integer> sender, Dispatcher dispatcher) {
        sender.sendValue(0);
    }

    private static void add1(Sender.Value<Integer> sender, int operand, Dispatcher dispatcher) {
        sender.sendValue(operand);
    }

    private static void add2(Sender.Value<Integer> sender, int operand1, int operand2, Dispatcher dispatcher) {
        int sum = operand1 + operand2;
        sender.sendValue(sum);
    }

    private static void add3(
            Sender.Value<Integer> sender, int operand1, int operand2, int operand3, Dispatcher dispatcher) {
        int sum = operand1 + operand2 + operand3;
        sender.sendValue(sum);
    }

    private static void add4(
            Sender.Value<Integer> sender,
            int operand1,
            int operand2,
            int operand3,
            int operand4,
            Dispatcher dispatcher) {
        int sum = operand1 + operand2 + operand3 + operand4;
        sender.sendValue(sum);
    }

    /** Test exchange. */
    private record TestExchange(
            FakeSender.Value<Integer> sender,
            Dispatcher dispatcher,
            String operand1,
            String operand2,
            String operand3,
            String operand4) {

        public static TestExchange of(String operand1, String operand2, String operand3, String operand4) {
            return new TestExchange(
                    FakeSender.Value.create(), StubDispatcher.get(), operand1, operand2, operand3, operand4);
        }

        public HttpOptional<Integer> extractOperand1() {
            return extractInt(operand1);
        }

        public HttpOptional<Integer> extractOperand2() {
            return extractInt(operand2);
        }

        public HttpOptional<Integer> extractOperand3() {
            return extractInt(operand3);
        }

        public HttpOptional<Integer> extractOperand4() {
            return extractInt(operand4);
        }

        private static HttpOptional<Integer> extractInt(String value) {
            try {
                int n = Integer.parseInt(value);
                return HttpOptional.of(n);
            } catch (NumberFormatException e) {
                return HttpOptional.empty(400);
            }
        }
    }
}
