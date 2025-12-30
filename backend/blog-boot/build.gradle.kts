import org.gradle.api.file.DuplicatesStrategy

plugins {
    id("org.springframework.boot")
}

val openTelemetryAppenderVersion = "1.28.0-alpha"
val jacksonDataBindNullable = "0.2.8"
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:$openTelemetryAppenderVersion")
    implementation(project(":blog-domain"))
    implementation(project(":blog-application"))
    implementation(project(":blog-adapters:persistence-jpa"))
    implementation(project(":blog-adapters:web-rest"))
    implementation(project(":blog-adapters:security-keycloak"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.openapitools:jackson-databind-nullable:$jacksonDataBindNullable")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

tasks.test {
    useJUnitPlatform()
}

val integrationTest: SourceSet by sourceSets.creating {
    java.srcDir("src/integrationTest/java")
    resources.srcDir("src/integrationTest/resources")

    compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
    runtimeClasspath += output + compileClasspath
}

configurations[integrationTest.implementationConfigurationName]
    .extendsFrom(configurations["testImplementation"])
configurations[integrationTest.runtimeOnlyConfigurationName]
    .extendsFrom(configurations["testRuntimeOnly"])

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath

    shouldRunAfter(tasks.test)
    useJUnitPlatform()
}

tasks.named("check") {
    dependsOn("integrationTest")
}

tasks.named<ProcessResources>("processIntegrationTestResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
