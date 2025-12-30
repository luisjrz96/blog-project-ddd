import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

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

// ---- helpers ----

fun jacocoClassDirs() = subprojects.flatMap { sp ->
	val projExcludes = jacocoExcludesByProject[sp.path].orEmpty()
	val excludes = projExcludes + commonExcludes

	listOf(
		fileTree("${sp.buildDir}/classes/java/main") { exclude(excludes) },
		fileTree("${sp.buildDir}/classes/kotlin/main") { exclude(excludes) }
	)
}

fun jacocoSourceDirs() = files(
	subprojects.map { file("${it.projectDir}/src/main/java") } +
			subprojects.map { file("${it.projectDir}/src/main/kotlin") }
)

fun JacocoReport.useExecutionDataFrom(testTasks: List<Test>) {
	executionData.setFrom(
		files(
			testTasks.mapNotNull { t ->
				t.extensions.findByType(JacocoTaskExtension::class.java)?.destinationFile
			}
		)
	)
}

// ---- reports ----

val jacocoRootUnitReport = tasks.register("jacocoRootUnitReport", JacocoReport::class) {
	group = "verification"
	description = "JaCoCo root report (unit tests only: test)."

	val unitTests = subprojects.flatMap { sp ->
		sp.tasks.withType<Test>()
			.matching { it.name == "test" }
			.toList()
	}

	dependsOn(unitTests)
	useExecutionDataFrom(unitTests)

	classDirectories.setFrom(jacocoClassDirs())
	sourceDirectories.setFrom(jacocoSourceDirs())
	additionalSourceDirs.setFrom(files(subprojects.map { file("${it.projectDir}/src/main/kotlin") }))

	reports {
		xml.required.set(true)
		html.required.set(true)
		csv.required.set(false)
	}
}

val jacocoRootAllReport = tasks.register("jacocoRootAllReport", JacocoReport::class) {
	group = "verification"
	description = "JaCoCo root report (unit + integration: test + integrationTest)."

	val allTests = subprojects.flatMap { sp ->
		sp.tasks.withType<Test>()
			.matching { it.name == "test" || it.name == "integrationTest" }
			.toList()
	}

	dependsOn(allTests)
	useExecutionDataFrom(allTests)

	classDirectories.setFrom(jacocoClassDirs())
	sourceDirectories.setFrom(jacocoSourceDirs())
	additionalSourceDirs.setFrom(files(subprojects.map { file("${it.projectDir}/src/main/kotlin") }))

	reports {
		xml.required.set(true)
		html.required.set(true)
		csv.required.set(false)
	}
}

tasks.named("check") {
	dependsOn(jacocoRootUnitReport)
	dependsOn(jacocoRootAllReport)
}
