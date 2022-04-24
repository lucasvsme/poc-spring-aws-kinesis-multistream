import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.logging.log4j", "log4j-api", properties["version.log4j"].toString())
    implementation("org.apache.logging.log4j", "log4j-core", properties["version.log4j"].toString())
    implementation("org.slf4j", "slf4j-simple", properties["version.slf4j"].toString())

    implementation("org.springframework", "spring-context", properties["version.spring"].toString())
    implementation("com.fasterxml.jackson.core", "jackson-databind", properties["version.jackson"].toString())
    implementation("com.amazonaws", "aws-java-sdk-s3", properties["version.aws.v1"].toString())
    implementation("software.amazon.kinesis", "amazon-kinesis-client", properties["version.aws.kcl"].toString())

    testImplementation("org.junit.jupiter", "junit-jupiter", properties["version.junit"].toString())
    testImplementation("org.mockito", "mockito-core", properties["version.mockito"].toString())
    testImplementation("org.springframework", "spring-test", properties["version.spring"].toString())
    testImplementation("org.testcontainers", "testcontainers", properties["version.testcontainers"].toString())
    testImplementation("org.testcontainers", "localstack", properties["version.testcontainers"].toString())
    testImplementation("org.testcontainers", "junit-jupiter", properties["version.testcontainers"].toString())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<JavaExec> {
    jvmArgs("--enable-preview")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    }
    finalizedBy(tasks.withType<JacocoReport>())
}
