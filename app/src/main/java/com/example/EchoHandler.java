package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;

public class EchoHandler implements RequestHandler<Map<String, String>, String> {
  @Override
  public String handleRequest(Map<String, String> input, Context context) {
    return input.getOrDefault("message", "no [message]");
  }
}
