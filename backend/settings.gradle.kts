rootProject.name = "blog"
include("blog-domain", "blog-boot")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}