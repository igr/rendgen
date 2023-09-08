plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation("org.neo4j:neo4j:5.11.0")
    implementation("org.neo4j.driver:neo4j-java-driver:5.11.0")

    implementation("com.github.javaparser:javaparser-core-serialization:3.25.4")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.25.4")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

application {
    mainClass.set("rendgen.AppKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
