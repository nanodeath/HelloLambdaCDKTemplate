package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class EchoHandlerTest {
  private final EchoHandler echoHandler = new EchoHandler();

  @Test
  void returnsMessage_IfProvidedMessage() {
    var output = echoHandler.handleRequest(Map.of("message", "hello"), null);

    assertThat(output).isEqualTo("hello");
  }

  @Test
  void returnsError_IfNoMessage() {
    var output = echoHandler.handleRequest(Map.of(), null);

    assertThat(output).isEqualTo("no [message]");
  }
}
