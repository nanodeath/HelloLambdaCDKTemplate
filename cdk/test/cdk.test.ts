import * as cdk from "aws-cdk-lib";
import { Template } from "aws-cdk-lib/assertions";
import * as Cdk from "../lib/cdk-stack";

test("Verifyng main stack", () => {
  const app = new cdk.App();
  const stack = new Cdk.CdkStack(app, "MyTestStack");
  const template = Template.fromStack(stack);

  template.hasResourceProperties("AWS::Lambda::Function", {
    Architectures: ["arm64"],
    FunctionName: "Echo-Sample",
    Handler: "com.example.EchoHandler",
    MemorySize: 1024,
    Runtime: "java17",
  });
  template.hasResourceProperties("AWS::Lambda::Function", {
    Architectures: ["arm64"],
    FunctionName: "Json-Sample",
    Handler: "com.example.JsonHandler",
    MemorySize: 1024,
    Runtime: "java17",
  });
  template.hasResourceProperties("AWS::ApiGateway::RestApi", {
    Name: "HelloHttp-REST",
  });
});
