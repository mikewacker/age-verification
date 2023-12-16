package org.example.age.api.infra;

import io.undertow.server.HttpHandler;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;

/**
 * Test service for {@link AddApi} whose corresponding {@link HttpHandler} uses {@link UndertowJsonApiHandler}'s
 * (and an {@link UndertowApiRouter}).
 *
 * <p>It also triggers an exception if the sum is 500.</p>
 */
public final class AddService implements AddApi {

    /** Creates an {@link HttpHandler} from an {@link AddService}. */
    public static HttpHandler createHandler() {
        AddApi api = new AddService();
        return AddApi.createHandler(api);
    }

    @Override
    public void add(Sender.Value<Integer> sender, int operand1, int operand2, Dispatcher dispatcher) {
        int sum = operand1 + operand2;
        if (sum == 500) {
            throw new RuntimeException();
        }

        sender.sendValue(sum);
    }

    @Override
    public void healthCheck(Sender.StatusCode sender, Dispatcher dispatcher) {
        sender.sendOk();
    }

    private AddService() {}
}
