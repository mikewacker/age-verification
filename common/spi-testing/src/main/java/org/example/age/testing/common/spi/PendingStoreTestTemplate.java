package org.example.age.testing.common.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.util.WebStageTesting.await;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.example.age.common.spi.PendingStore;
import org.junit.jupiter.api.Test;

public abstract class PendingStoreTestTemplate {

    @Test
    public void putThenGet() {
        await(store().put("key1", 1, expiresIn()));
        Optional<Integer> maybeValue = await(store().tryGet("key1"));
        assertThat(maybeValue).hasValue(1);
    }

    @Test
    public void putThenRemoveThenGet() {
        await(store().put("key2", 1, expiresIn()));
        Optional<Integer> maybeValue1 = await(store().tryRemove("key2"));
        assertThat(maybeValue1).hasValue(1);
        Optional<Integer> maybeValue2 = await(store().tryGet("key2"));
        assertThat(maybeValue2).isEmpty();
    }

    @Test
    public void putThenExpireThenGet() throws InterruptedException {
        await(store().put("key3", 1, expiresIn(Duration.ofMillis(2))));
        Thread.sleep(4);
        Optional<Integer> maybeValue = await(store().tryRemove("key3"));
        assertThat(maybeValue).isEmpty();
    }

    @Test
    public void putExpiredThenGet() {
        await(store().put("key4", 1, expiresIn(Duration.ofMinutes(-1))));
        Optional<Integer> maybeValue = await(store().tryRemove("key4"));
        assertThat(maybeValue).isEmpty();
    }

    protected static OffsetDateTime expiresIn() {
        return expiresIn(Duration.ofMinutes(5));
    }

    protected static OffsetDateTime expiresIn(Duration duration) {
        return OffsetDateTime.now(ZoneOffset.UTC).plus(duration).truncatedTo(ChronoUnit.MILLIS);
    }

    protected abstract PendingStore<Integer> store();
}
