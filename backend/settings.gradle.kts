rootProject.name = "blog"
include("blog-domain", "blog-boot", "blog-application")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}