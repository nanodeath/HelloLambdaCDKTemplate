import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    // Java
    id("java")
    // Kotlin
    // kotlin("jvm") version "1.8.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

//kotlin {
//    jvmToolchain(17)
//}

val awsProfile =
    if (hasProperty("aws.profile"))
        findProperty("aws.profile")!!.toString().takeUnless { it.isBlank() }
    else null

fun cdkCommand(vararg args: String): List<String> {
    val command = shellCommand("cdk", *args).toMutableList()
    if (awsProfile != null) {
        command += listOf("--profile", awsProfile)
    }
    return command
}

fun shellCommand(vararg args: String): List<String> =
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        listOf("cmd", "/c", *args)
    } else {
        listOf(*args)
    }

val cdkFix = tasks.register<Exec>("cdkFormat") {
    group = "verification"
    description = "Reformats CDK code using fix-format npm script."
    commandLine(shellCommand("npm", "run", "fix-format"))
    workingDir("cdk")
}
tasks.register("autoformat") {
    group = "verification"
    description = "Reformats all code in the project."
    dependsOn(":app:spotlessApply", cdkFix)
}

tasks.register<Exec>("deploy") {
    group = "deployment"
    description = "Deploys using CDK and a full CloudFormation update."
    dependsOn(":app:buildZip", cdkFix)
    commandLine(cdkCommand("deploy", "--require-approval", "never"))
    workingDir("cdk")
}

tasks.register<Exec>("deployHotswap") {
    group = "deployment"
    description = "Deploys using CDK and a hotswap-type CloudFormation update."
    dependsOn(":app:buildZip", cdkFix)
    commandLine(cdkCommand("deploy", "--hotswap", "--require-approval", "never"))
    workingDir("cdk")
}
