package org.example.age.testing.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class FakeCodeSenderTest {

    private FakeCodeSender sender;

    @BeforeEach
    public void createCodeSender() {
        sender = FakeCodeSender.create();
    }

    @Test
    public void sendAndGet() {
        assertThat(sender.tryGet()).isEmpty();
        sender.sendError(403);
        assertThat(sender.tryGet()).hasValue(403);
    }

    @Test
    public void error_SendTwice() {
        sender.sendError(403);
        assertThatThrownBy(() -> sender.sendError(200))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("response was already sent");
    }
}
