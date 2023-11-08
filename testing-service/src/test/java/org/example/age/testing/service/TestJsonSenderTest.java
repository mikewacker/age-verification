package org.example.age.testing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.age.common.api.HttpOptional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class TestJsonSenderTest {

    private TestJsonSender<String> sender;

    @BeforeEach
    public void createJsonSender() {
        sender = TestJsonSender.create();
    }

    @Test
    public void sendAndGet() {
        assertThat(sender.wasSent()).isFalse();
        sender.sendBody("test");
        assertThat(sender.wasSent()).isTrue();
        assertThat(sender.get()).isEqualTo(HttpOptional.of("test"));
    }

    @Test
    public void error_GetWithoutSend() {
        assertThatThrownBy(sender::get)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("response has not been sent");
    }

    @Test
    public void error_SendTwice() {
        sender.sendBody("test");
        assertThatThrownBy(() -> sender.sendBody("test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("response was already sent");
    }
}
