package org.example.age.module.common;

import io.dropwizard.core.setup.Environment;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.HttpHeaders;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

/** Implementation of {@link RequestContext} for Dropwizard. */
@Singleton
final class DropwizardRequestContext implements RequestContext {

    private Provider<HttpHeaders> httpHeadersProvider;

    @Inject
    public DropwizardRequestContext(Environment env) {
        env.jersey().register(new RequestContextInjector());
    }

    @Override
    public HttpHeaders httpHeaders() {
        return httpHeadersProvider.get();
    }

    /** Injects the request context using Jersey/HK2. */
    private final class RequestContextInjector implements ContainerLifecycleListener {

        @Override
        public void onStartup(Container container) {
            InjectionManager injectionManager =
                    container.getApplicationHandler().getInjectionManager();
            httpHeadersProvider = injectionManager.getInstance(new ProviderType(HttpHeaders.class));
        }

        @Override
        public void onReload(Container container) {}

        @Override
        public void onShutdown(Container container) {}
    }

    /** Parameterized type for a provider. */
    private record ProviderType(Class<?> type) implements ParameterizedType {

        @Override
        public Type getRawType() {
            return Provider.class;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {type};
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
