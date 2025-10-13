package org.example.age.testing.client;

import jakarta.ws.rs.WebApplicationException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

/** Utilities for testing an endpoint class directly. */
public final class TestAsyncEndpoints {

    /** Decorates the endpoint by converting uncaught exceptions to failed stages. */
    @SuppressWarnings("unchecked")
    public static <A> A test(A endpoint, Class<A> apiType) {
        return (A) Proxy.newProxyInstance(
                apiType.getClassLoader(), new Class<?>[] {apiType}, new TestInvocationHandler(endpoint));
    }

    /** Creates a client for the endpoint. */
    public static <C> C client(Object endpoint, Class<?> apiType, Class<C> clientType) {
        return client(() -> endpoint, apiType, clientType);
    }

    /** Creates a client for the endpoint. */
    @SuppressWarnings("unchecked")
    public static <C> C client(Supplier<?> endpointRef, Class<?> apiType, Class<C> clientType) {
        return (C) Proxy.newProxyInstance(
                clientType.getClassLoader(),
                new Class<?>[] {clientType},
                new ClientInvocationHandler(endpointRef, apiType));
    }

    private TestAsyncEndpoints() {} // static class

    /** Converts an uncaught exception to a failed stage. */
    private record TestInvocationHandler(Object endpoint) implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(endpoint, args);
            } catch (InvocationTargetException e) {
                return CompletableFuture.failedFuture(e.getTargetException());
            }
        }
    }

    /** Converts the asynchronous stage to a Retrofit call. */
    private record ClientInvocationHandler(Supplier<?> endpointRef, Class<?> apiType) implements InvocationHandler {

        @SuppressWarnings("unchecked")
        @Override
        public Object invoke(Object proxy, Method clientMethod, Object[] args) throws Throwable {
            Method apiMethod = apiType.getMethod(clientMethod.getName(), clientMethod.getParameterTypes());
            CompletionStage<Object> stage = (CompletionStage<Object>) apiMethod.invoke(endpointRef.get(), args);
            return Calls.defer(() -> completeAsCall(stage));
        }

        /** Completes the asynchronous stage, converting the result to a Retrofit call. */
        private static Call<Object> completeAsCall(CompletionStage<Object> stage) {
            try {
                Object value = stage.toCompletableFuture().get(1, TimeUnit.SECONDS);
                return Calls.response(value);
            } catch (ExecutionException e) {
                int errorCode = getErrorCode(e.getCause());
                Response<Object> response = Response.error(errorCode, ResponseBody.create("", null));
                return Calls.response(response);
            } catch (TimeoutException | InterruptedException e) {
                return Calls.failure(e);
            }
        }

        /** Gets the HTTP error code. */
        private static int getErrorCode(Throwable t) {
            return (t instanceof WebApplicationException e) ? e.getResponse().getStatus() : 500;
        }
    }
}
