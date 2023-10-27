package org.example.age.common.utils.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.undertow.server.HttpServerExchange;
import java.time.Duration;
import org.example.age.common.store.internal.PendingStore;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.testing.TestExchanges;
import org.junit.jupiter.api.Test;

public final class PendingStoreUtilsTest {

    @Test
    public void putAndGet() {
        PendingStore<Integer, String> store = PendingStore.create();
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
}
