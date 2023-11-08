package org.example.age.testing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.undertow.util.StatusCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class TestCodeSenderTest {

    private TestCodeSender sender;

    @BeforeEach
    public void createCodeSender() {
        sender = TestCodeSender.create();
    }

    @Test
    public void sendAndGet() {
        assertThat(sender.wasSent()).isFalse();
        sender.sendError(StatusCodes.FORBIDDEN);
        assertThat(sender.wasSent()).isTrue();
        assertThat(sender.get()).isEqualTo(403);
    }

    @Test
    public void error_GetWithoutSend() {
        assertThatThrownBy(sender::get)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("response has not been sent");
    }

    @Test
    public void error_SendTwice() {
        sender.sendError(StatusCodes.FORBIDDEN);
        assertThatThrownBy(() -> sender.sendError(StatusCodes.OK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("response was already sent");
    }
}
