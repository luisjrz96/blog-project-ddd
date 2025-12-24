plugins {
    id("java")
}

group = "com.luisjrz96.blog.application"
version = "0.0.1-SNAPSHOT"
val mockitoVersion = "5.11.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":blog-domain"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Mockito
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
}

tasks.test {
    useJUnitPlatform()
}
