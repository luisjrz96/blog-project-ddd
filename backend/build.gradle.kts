import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
	id("java-library")
	id("org.springframework.boot") version "3.5.6" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	id("com.diffplug.spotless") version "8.0.0" apply false
	id("jacoco")
}

allprojects {
	group = "com.luisjrz96.blog"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "java-library")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "com.diffplug.spotless")
	apply(plugin = "jacoco")

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
	}

	extensions.configure<JacocoPluginExtension> {
		toolVersion = "0.8.11"
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	configure<SpotlessExtension> {
		java {
			target("src/**/*.java")
			googleJavaFormat("1.17.0")
			removeUnusedImports()
			forbidWildcardImports()
			importOrder("java", "javax", "org", "com", "")
		}

		kotlinGradle {
			target("*.gradle.kts")
			ktlint("1.3.1")
		}

		format("misc") {
			target("*.md", "*.yml", "*.yaml", "*.json", ".gitignore")
			trimTrailingWhitespace()
			endWithNewline()
		}
	}

}

// Map subproject name -> list of exclude patterns for that subproject
val jacocoExcludesByProject: Map<String, List<String>> = mapOf(
	":blog-boot" to listOf(
		"**/BlogApplication**",
		"**/config/**"
	),
	":blog-application" to listOf(
		"**/blog/**/command/**",
		"**/blog/**/query/**",
		"**/shared/error/**",
		"**/shared/security/**"
	),
	":blog-adapters:web-rest" to listOf(
		"**/dto/**"
	),
	":blog-adapters:persistence-jpa" to listOf(
		"**/blog/**/**ViewEntity.*"
	)
)

// common excludes applied to every subproject
val commonExcludes = listOf("**/config/**", "**/internal/**")

val jacocoRootReport = tasks.register("jacocoRootReport", JacocoReport::class) {
	// ensure tests in subprojects run first
	dependsOn(subprojects.flatMap { it.tasks.matching { t -> t.name == "test" } })

	// collect execution data from subprojects
	executionData.setFrom(files(subprojects.map { file("${it.buildDir}/jacoco/test.exec") }))

	// build classDirectories and apply per-project excludes plus common excludes
	val classDirs = subprojects.map { sp ->
		fileTree("${sp.buildDir}/classes/java/main") {
			println("Project: ${sp.path}, excludes: ${jacocoExcludesByProject[sp.path]}")

			val projExcludes = jacocoExcludesByProject[sp.path].orEmpty()
			exclude(projExcludes + commonExcludes)
		}
	}
	classDirectories.setFrom(classDirs)

	// collect source directories (java & kotlin)
	sourceDirectories.setFrom(files(subprojects.map { file("${it.projectDir}/src/main/java") } +
			subprojects.map { file("${it.projectDir}/src/main/kotlin") }))

	additionalSourceDirs.setFrom(files(subprojects.map { file("${it.projectDir}/src/main/kotlin") }))

	reports {
		xml.required.set(true)
		html.required.set(true)
		csv.required.set(false)
	}
}

// Optional: make root report run with check
tasks.named("check") {
	dependsOn(jacocoRootReport)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
