plugins {
    java
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework:spring-framework-bom:6.1.1"))
    implementation("org.springframework:spring-context")
    testImplementation("org.springframework:spring-test")

    implementation(platform("org.slf4j:slf4j-bom:2.0.9"))
    implementation("org.slf4j:slf4j-simple")

    implementation(platform("org.apache.logging.log4j:log4j-bom:2.22.0"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")

    implementation(platform("org.testcontainers:testcontainers-bom:1.19.3"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:localstack")
    testImplementation("org.testcontainers:junit-jupiter")

    implementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(platform("org.mockito:mockito-bom:5.8.0"))
    testImplementation("org.mockito:mockito-core")

    implementation(platform("com.fasterxml.jackson:jackson-bom:2.16.0"))
    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation(platform("com.amazonaws:aws-java-sdk-bom:1.12.604"))
    implementation("com.amazonaws:aws-java-sdk-s3")

    implementation("software.amazon.kinesis:amazon-kinesis-client:2.5.3")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}
