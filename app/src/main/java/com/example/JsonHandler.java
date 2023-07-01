package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonHandler implements RequestHandler<Map<String, String>, String> {
  @Override
  public String handleRequest(Map<String, String> input, Context context) {
    return "{"
        + input.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining(";"))
        + "}";
  }
}
