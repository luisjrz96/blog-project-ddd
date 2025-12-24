plugins {
    id("java")
    id("org.openapi.generator") version "7.8.0"
}

group = "com.luisjrz96.blog.adapters.web"
version = "0.0.1-SNAPSHOT"
val springDependenciesVersion = "3.5.5"
val openApiVersion = "2.8.13"
val swaggerParserVersion = "2.1.21"
val openApiJacksonVersion = "0.2.8"
val mapStructVersion = "1.5.5.Final"
val mapStructBindingVersion = "0.2.0"
val lombokVersion = "1.18.34"
val junitVersion = "5.10.0"
val mockitoVersion = "5.11.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":blog-domain"))
    implementation(project(":blog-application"))

    implementation(platform("org.springframework.boot:spring-boot-dependencies:$springDependenciesVersion"))
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // observability
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.micrometer:micrometer-registry-prometheus")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVersion")
    implementation("io.swagger.parser.v3:swagger-parser:$swaggerParserVersion")
    implementation("org.openapitools:jackson-databind-nullable:$openApiJacksonVersion")
    implementation("org.mapstruct:mapstruct:$mapStructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$mapStructBindingVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Mockito
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/static/openapi/blog-api.yml")
    outputDir.set(layout.buildDirectory.dir("generated").map { it.asFile.absolutePath })
    apiPackage.set("com.luisjrz96.blog.adapters.web.api")
    modelPackage.set("com.luisjrz96.blog.adapters.web.dto")
    invokerPackage.set("com.luisjrz96.blog.adapters.web.invoker")
    configOptions.set(
        mapOf(
            "interfaceOnly" to "true",
            "useTags" to "true",
            "dateLibrary" to "java8",
            "skipDefaultInterface" to "true",
            "useSpringBoot3" to "true",
            "jakarta" to "true",
        ),
    )
}

sourceSets["main"].java {
    srcDir(
        layout.buildDirectory
            .dir("generated/src/main/java")
            .get()
            .asFile,
    )
}

tasks.test {
    useJUnitPlatform()
}

tasks.named("compileJava") {
    dependsOn(tasks.named("openApiGenerate"))
}
