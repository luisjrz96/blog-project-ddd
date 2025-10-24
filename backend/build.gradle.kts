import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
	id("java-library")
	id("org.springframework.boot") version "3.5.6" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	id("com.diffplug.spotless") version "8.0.0" apply false
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

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	configure<SpotlessExtension> {
		java {
			target("src/**/*.java")
			googleJavaFormat("1.17.0")
			removeUnusedImports()
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

tasks.withType<Test> {
	useJUnitPlatform()
}
