plugins {
    id("org.springframework.boot")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":blog-application"))

    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
