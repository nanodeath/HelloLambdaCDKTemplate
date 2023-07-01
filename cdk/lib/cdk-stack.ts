import * as cdk from "aws-cdk-lib";
import * as apigateway from "aws-cdk-lib/aws-apigateway";
import * as lambda from "aws-cdk-lib/aws-lambda";
import { type Construct } from "constructs";
import { ApiGatewayToLambda } from "@aws-solutions-constructs/aws-apigateway-lambda";
import {
  WafwebaclToApiGatewayProps,
  WafwebaclToApiGateway,
} from "@aws-solutions-constructs/aws-wafwebacl-apigateway";

export class CdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // The code that defines your stack goes here

    // example resource
    // const queue = new sqs.Queue(this, 'CdkQueue', {
    //   visibilityTimeout: cdk.Duration.seconds(300)
    // });

    // Factoring dependencies out into a separate layer isn't required,
    // but might speed up deployments.
    const appDependenciesLayer = new lambda.LayerVersion(
      this,
      "AppDependencies",
      {
        code: lambda.Code.fromAsset(
          "../app/build/distributions/deps-1.0-SNAPSHOT.zip"
        ),
        layerVersionName: "AppDependencies",
        description: "Dependencies common to all lambdas",
        compatibleRuntimes: [lambda.Runtime.JAVA_17],
        compatibleArchitectures: [
          lambda.Architecture.ARM_64,
          lambda.Architecture.X86_64,
        ],
      }
    );

    const commonLambdaProps: Omit<
      lambda.FunctionProps,
      "functionName" | "handler"
    > = {
      code: lambda.Code.fromAsset(
        "../app/build/distributions/app-1.0-SNAPSHOT.zip"
      ),
      runtime: lambda.Runtime.JAVA_17,
      memorySize: 1024,
      architecture: lambda.Architecture.ARM_64,
      layers: [appDependenciesLayer],
    };

    // Here we have a couple basic example lambdas we define directly and can later invoke in the AWS console.
    const echoLambda = new lambda.Function(this, "EchoLambda", {
      ...commonLambdaProps,
      functionName: "Echo-Sample",
      handler: "com.example.EchoHandler",
    });

    const jsonLambda = new lambda.Function(this, "JsonLambda", {
      ...commonLambdaProps,
      functionName: "Json-Sample",
      handler: "com.example.JsonHandler",
    });
    // Here's a lambda that speaks API Gateway's REST protocol, plus the API Gateway component that goes with it
    const httpLambda = new lambda.Function(this, "HelloHttpLambda", {
      ...commonLambdaProps,
      functionName: "HelloHttp-Sample",
      handler: "com.example.HelloHttpHandler",
    });

    // You can invoke the above lambda by going into API Gateway in the console and invoking this endpoint.
    const restApi = new apigateway.LambdaRestApi(this, "HelloHttpApi", {
      restApiName: "HelloHttp-REST",
      handler: httpLambda,
    });

    // Here's another way of doing the above two constructs (lambda + API Gateway), but combined into one construct
    // using an AWS Solutions construct.
    const apiGatewayToLambda = new ApiGatewayToLambda(
      this,
      "ApiGatewayHelloHttp",
      {
        lambdaFunctionProps: {
          // This defines the lambda inside this construct. You can also use the existingLambdaObj property
          // to define it outside the construct, if you like.
          ...commonLambdaProps,
          functionName: "HelloHttp-Sample-2",
          handler: "com.example.HelloHttpHandler",
        },
        apiGatewayProps: {
          restApiName: "HelloHttp-REST-2",
        },
      }
    );

    // Here we're using another AWS Solutions construct to set up WAF (web firewall) with reasonable defaults.
    // https://docs.aws.amazon.com/solutions/latest/constructs/aws-wafwebacl-apigateway.html

    // NOTE: this by default uses the Bot Control rule set, which isn't free! Enabling WAF, and Bot Control specifically,
    // can add up to somewhere around $20 USD/mo even with no traffic. Not much, but probably too much for a prototype.

    // new WafwebaclToApiGateway(this, "WAFHelloHttp", {
    //   existingApiGatewayInterface: apiGatewayToLambda.apiGateway,
    // });
  }
}
