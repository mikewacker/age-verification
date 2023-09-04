package org.example.age.common.client;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.net.SocketTimeoutException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.xnio.IoUtils;

/** Callback for a backend request made as part of a frontend exchange. */
final class UndertowCallback implements Callback {

    private final SuccessCallback backendSuccessCallback;
    private final HttpServerExchange frontendExchange;

    /** Creates a callback. */
    public static Callback create(SuccessCallback backendSuccessCallback, HttpServerExchange frontendExchange) {
        return new UndertowCallback(backendSuccessCallback, frontendExchange);
    }

    @Override
    public void onResponse(Call call, Response backendResponse) {
        // Check the status code.
        if (!backendResponse.isSuccessful()) {
            int frontendStatusCode =
                    (backendResponse.code() / 100 == 5) ? StatusCodes.BAD_GATEWAY : StatusCodes.INTERNAL_SERVER_ERROR;
            sendFrontendError(frontendStatusCode);
            return;
        }

        // Read the response body.
        byte[] backendResponseBody;
        try {
            backendResponseBody = backendResponse.body().bytes();
        } catch (IOException e) {
            handleBackendIoError(e);
            return;
        }

        // Send the response for the exchange.
        sendFrontendResponse(backendResponse, backendResponseBody);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        handleBackendIoError(e);
    }

    /** Handles an IO error for the backend response. */
    private void handleBackendIoError(IOException e) {
        int frontendStatusCode =
                (e instanceof SocketTimeoutException) ? StatusCodes.GATEWAY_TIME_OUT : StatusCodes.BAD_GATEWAY;
        sendFrontendError(frontendStatusCode);
    }

    /** Sends the response for the frontend exchange. */
    private void sendFrontendResponse(Response backendResponse, byte[] backendResponseBody) {
        if (!checkFrontendResponseNotStarted()) {
            return;
        }

        try {
            backendSuccessCallback.onSuccess(backendResponse, backendResponseBody, frontendExchange);
        } catch (Exception e) {
            sendFrontendError(StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    /** Sends an error code for the frontend exchange. */
    private void sendFrontendError(int statusCode) {
        if (!checkFrontendResponseNotStarted()) {
            return;
        }

        frontendExchange.setStatusCode(statusCode);
        frontendExchange.endExchange();
    }

    /**
     * Checks that the frontend response has not started, returning true if so.
     *
     * <p>Safely closes the connection if the response has started.</p>
     */
    private boolean checkFrontendResponseNotStarted() {
        if (frontendExchange.isResponseStarted()) {
            IoUtils.safeClose(frontendExchange.getConnection());
            return false;
        }

        return true;
    }

    private UndertowCallback(SuccessCallback backendSuccessCallback, HttpServerExchange frontendExchange) {
        this.backendSuccessCallback = backendSuccessCallback;
        this.frontendExchange = frontendExchange;
    }
}
