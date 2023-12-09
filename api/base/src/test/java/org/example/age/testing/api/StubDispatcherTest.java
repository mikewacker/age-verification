package org.example.age.testing.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.ScheduledExecutor;
import org.junit.jupiter.api.Test;

public final class StubDispatcherTest {

    @Test
    public void isInIoThread() {
        Dispatcher dispatcher = StubDispatcher.get();
        assertThat(dispatcher.isInIoThread()).isTrue();
    }

    @Test
    public void getIoThread() {
        Dispatcher dispatcher = StubDispatcher.get();
        ScheduledExecutor ioThread = dispatcher.getIoThread();
        ioThread.execute(() -> {});
        ScheduledExecutor.Key key = ioThread.executeAfter(() -> {}, Duration.ofMinutes(1));
        assertThat(key).isNotNull();

        assertThat(key.cancel()).isFalse();
    }

    @Test
    public void getWorker() {
        Dispatcher dispatcher = StubDispatcher.get();
        ExecutorService worker = dispatcher.getWorker();
        worker.execute(() -> {});
        Future<?> future1 = worker.submit(() -> {});
        assertThat(future1).isNotNull();
        Future<String> future2 = worker.submit(() -> {}, "value");
        assertThat(future2).isNotNull();
        Future<String> future3 = worker.submit(() -> "value");
        assertThat(future3).isNotNull();

        assertThatThrownBy(future3::get).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> future3.get(1, TimeUnit.SECONDS)).isInstanceOf(UnsupportedOperationException.class);
        assertThat(future3.cancel(true)).isFalse();
        assertThat(future3.isDone()).isFalse();
        assertThat(future3.isCancelled()).isFalse();
    }
}
