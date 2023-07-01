plugins {
    id("java")
    id("com.diffplug.spotless") version "6.19.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.2")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.5.1")

    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")

    // Kotlin
    //testImplementation(kotlin("test"))
}

spotless {
    java {
        removeUnusedImports()
        googleJavaFormat()
        formatAnnotations()
    }
}

tasks.test {
    useJUnitPlatform()
}

val buildAppZip = tasks.register<Zip>("buildAppZip") {
    group = "build"
    description = "Packages the application into a single app JAR."
    dependsOn(tasks.jar)
    archiveBaseName.set("app")
    into("lib") {
        from(tasks.jar)
    }
}

val buildDepsZip = tasks.register<Zip>("buildDepsZip") {
    group = "build"
    description = "Packages runtime dependencies into a single deps JAR."
    archiveBaseName.set("deps")
    into("java/lib/") {
        from(configurations.runtimeClasspath)
    }
}

val buildUberZip = tasks.register<Zip>("buildUberZip") {
    dependsOn(tasks.jar)
    // This task is currently unused, but here for your convenience. You might be interested in switching from the
    // app+lib layer approach used by default; this task will create a single JAR containing both the application
    // and its dependencies.
    into("lib") {
        from(tasks.jar)
        from(configurations.runtimeClasspath)
    }
}

tasks.register("buildZip") {
    group = "build"
    description = "Packages application and dependencies into JARs, ready for deployment."
    dependsOn(buildAppZip, buildDepsZip)
}