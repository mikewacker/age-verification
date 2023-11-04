package org.example.age.common.base.utils.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import java.time.Duration;
import javax.inject.Singleton;
import org.example.age.common.base.store.InMemoryPendingStoreFactoryModule;
import org.example.age.common.base.store.PendingStore;
import org.example.age.common.base.store.PendingStoreFactory;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.testing.client.TestExchanges;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class PendingStoreUtilsTest {

    private PendingStore<Integer, String> store;

    @BeforeEach
    public void createPendingStore() {
        PendingStoreFactory storeFactory = TestComponent.createPendingStoreFactory();
        store = storeFactory.create();
    }

    @Test
    public void putAndGet() {
        VerificationSession session = createVerificationSession();
        HttpServerExchange exchange = createStubExchange();
        PendingStoreUtils.putForVerificationSession(store, 1, "a", session, exchange);
        assertThat(store.tryGet(1)).hasValue("a");
    }

    private static VerificationSession createVerificationSession() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofHours(1));
        return VerificationSession.create(request);
    }

    private static HttpServerExchange createStubExchange() {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addStubIoThread(exchange);
        return exchange;
    }

    /** Dagger component that provides a {@link PendingStoreFactory}. */
    @Component(modules = InMemoryPendingStoreFactoryModule.class)
    @Singleton
    interface TestComponent {

        static PendingStoreFactory createPendingStoreFactory() {
            TestComponent component = DaggerPendingStoreUtilsTest_TestComponent.create();
            return component.pendingStoreFactory();
        }

        PendingStoreFactory pendingStoreFactory();
    }
}
