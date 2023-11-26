package org.example.age.testing.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xnio.XnioExecutor;

public final class FakeXnioExecutorTest {

    private FakeXnioExecutor executor;

    private Runnable command;
    private boolean commandWasRun;

    @BeforeEach
    public void createFakeXnioExecutorEtAl() {
        executor = FakeXnioExecutor.create();
        command = () -> commandWasRun = true;
        commandWasRun = false;
    }

    @Test
    public void execute() {
        executor.execute(command);
        assertThat(commandWasRun).isTrue();
    }

    @Test
    public void executeAfter() {
        executor.executeAfter(command, 1, TimeUnit.SECONDS);
        assertThat(commandWasRun).isFalse();

        FakeXnioExecutor.ScheduledTask scheduledTask = executor.getLastScheduledTask();
        assertThat(scheduledTask.getTime()).isEqualTo(1L);
        assertThat(scheduledTask.getUnit()).isEqualTo(TimeUnit.SECONDS);
        scheduledTask.run();
        assertThat(commandWasRun).isTrue();
    }

    @Test
    public void executeAtInterval() {
        executor.executeAtInterval(command, 1, TimeUnit.SECONDS);
        assertThat(commandWasRun).isFalse();

        FakeXnioExecutor.ScheduledTask scheduledTask = executor.getLastScheduledTask();
        assertThat(scheduledTask.getTime()).isEqualTo(1L);
        assertThat(scheduledTask.getUnit()).isEqualTo(TimeUnit.SECONDS);
        scheduledTask.run();
        assertThat(commandWasRun).isTrue();
    }

    @Test
    public void remove() {
        XnioExecutor.Key key = executor.executeAfter(command, 1, TimeUnit.SECONDS);
        boolean wasRemoved = key.remove();
        assertThat(wasRemoved).isTrue();

        FakeXnioExecutor.ScheduledTask scheduledTask = executor.getLastScheduledTask();
        scheduledTask.run();
        assertThat(commandWasRun).isFalse();

        boolean wasRemovedAgain = key.remove();
        assertThat(wasRemovedAgain).isFalse();
    }

    @Test
    public void runAndRemove() {
        XnioExecutor.Key key = executor.executeAfter(command, 1, TimeUnit.SECONDS);
        FakeXnioExecutor.ScheduledTask scheduledTask = executor.getLastScheduledTask();
        scheduledTask.run();
        assertThat(commandWasRun).isTrue();

        boolean wasRemoved = key.remove();
        assertThat(wasRemoved).isFalse();
    }
}
