package org.example.age.testing.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.example.age.api.ScheduledExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class FakeScheduledExecutorTest {

    private FakeScheduledExecutor executor;

    private Runnable command;
    private boolean commandWasRun;

    @BeforeEach
    public void createFakeScheduledExecutorEtAl() {
        executor = FakeScheduledExecutor.create();
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
        executor.executeAfter(command, Duration.ofMinutes(1));
        assertThat(commandWasRun).isFalse();

        FakeScheduledExecutor.ScheduledTask scheduledTask = executor.getLastScheduledTask();
        assertThat(scheduledTask.getDelay()).isEqualTo(Duration.ofMinutes(1));
        scheduledTask.run();
        assertThat(commandWasRun).isTrue();
    }

    @Test
    public void cancel() {
        ScheduledExecutor.Key key = executor.executeAfter(command, Duration.ofMinutes(1));
        boolean wasCancelled = key.cancel();
        assertThat(wasCancelled).isTrue();

        FakeScheduledExecutor.ScheduledTask scheduledTask = executor.getLastScheduledTask();
        scheduledTask.run();
        assertThat(commandWasRun).isFalse();

        boolean wasCancelledAgain = key.cancel();
        assertThat(wasCancelledAgain).isFalse();
    }

    @Test
    public void runAndCancel() {
        ScheduledExecutor.Key key = executor.executeAfter(command, Duration.ofMinutes(1));
        FakeScheduledExecutor.ScheduledTask scheduledTask = executor.getLastScheduledTask();
        scheduledTask.run();
        assertThat(commandWasRun).isTrue();

        boolean wasCancelled = key.cancel();
        assertThat(wasCancelled).isFalse();
    }
}
