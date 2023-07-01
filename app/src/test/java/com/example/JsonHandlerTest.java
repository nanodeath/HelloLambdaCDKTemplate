package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonHandlerTest {
  private final JsonHandler jsonHandler = new JsonHandler();

  @Test
  void convertsMap_ToString() {
    var output = jsonHandler.handleRequest(Map.of("foo", "bar", "hello", "world"), null);

    assertThat(output).isEqualTo("{foo=bar;hello=world}");
  }
}
