package org.example.age.testing.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class FakeStatusCodeSenderTest {

    private FakeStatusCodeSender sender;

    @BeforeEach
    public void createStatusCodeSender() {
        sender = FakeStatusCodeSender.create();
    }

    @Test
    public void sendAndGet() {
        sender.sendErrorCode(403);
        assertThat(sender.tryGet()).hasValue(403);
    }

    @Test
    public void tryGet() {
        assertThat(sender.tryGet()).isEmpty();
    }

    @Test
    public void error_SendTwice() {
        sender.sendErrorCode(403);
        assertThatThrownBy(() -> sender.sendErrorCode(200))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("response was already sent");
    }
}
