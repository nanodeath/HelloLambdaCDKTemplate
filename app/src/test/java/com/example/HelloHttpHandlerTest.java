package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Test;

class HelloHttpHandlerTest {
  private final HelloHttpHandler helloHttpHandler = new HelloHttpHandler();

  @Test
  void echosBack_HttpMethod() {
    var output =
        helloHttpHandler.handleRequest(
            new APIGatewayProxyRequestEvent().withHttpMethod("GET"), null);

    assertThat(output)
        .isEqualTo(
            new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("<h1>Hello world! You are calling using <b>GET</b></h1>"));
  }
}
