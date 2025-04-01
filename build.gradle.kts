import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.kotlinJvm)
	alias(libs.plugins.kotlinSpring) apply false
}

extra["projectVersion"] = getEnv("VERSION", "latest")
extra["mavenName"] = getEnv("MAVEN_NAME")
extra["mavenSecret"] = getEnv("MAVEN_SECRET")
extra["initProjectName"] = "jwc-app"

// only for java classes
java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_17)
		freeCompilerArgs.set(listOf("-Xjsr305=strict"))
	}
}

allprojects {
	repositories {
		mavenCentral()
		mavenLocal()
		maven { url = uri("https://m2.dv8tion.net/releases") }
		maven { url = uri("https://jitpack.io") }
		maven {
			url = uri("https://m2.miloszgilga.pl/private")
			credentials {
				username = getProperty("mavenName")
				password = getProperty("mavenSecret")
			}
		}
	}
	group = "pl.jwizard"
	version = getProperty("projectVersion")
}

subprojects {
	val libs = rootProject.libs

	apply(plugin = getPluginId(libs.plugins.kotlinJvm))
	apply(plugin = getPluginId(libs.plugins.kotlinSpring))

	// apply library plugin only for submodules without main class
	if (project.name != getProperty("initProjectName")) {
		apply(plugin = "java-library")
	}

	dependencies {
		implementation(libs.jwizardLib)
		implementation(libs.kotlin)
		implementation(libs.kotlinReflect)
		implementation(libs.logbackClassic)

		testImplementation(libs.junitJupiter)
		testImplementation(libs.junitJupiterEngine)
	}

	tasks {
		test {
			useJUnitPlatform()
			testLogging {
				events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
			}
		}
	}

	configurations.configureEach {
		exclude(group = "commons-logging", module = "commons-logging")
	}
}

tasks {
	register("shadowJar") {
		group = "build"
		dependsOn(":${getProperty("initProjectName")}:shadowJar")
	}

	clean {
		doLast {
			val binDir = file("$projectDir/.bin")
			if (binDir.exists()) {
				binDir.deleteRecursively()
			}
		}
	}
}

// retrieves the value of an environment variable, with a fallback to a default value
fun getEnv(name: String, defValue: String = "") = System.getenv("JWIZARD_$name") ?: defValue

// gets the plugin ID from a given PluginDependency provider
fun getPluginId(accessor: Provider<PluginDependency>) = accessor.get().pluginId

// retrieves root property
fun getProperty(name: String) = rootProject.extra[name] as String
