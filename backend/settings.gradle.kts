rootProject.name = "blog"
include("blog-domain", "blog-boot", "blog-application", "blog-adapters", "blog-adapters:persistence-jpa",
    "blog-adapters:web-rest","blog-adapters:security-keycloak")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}