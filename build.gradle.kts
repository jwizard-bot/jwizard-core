/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.time.LocalDateTime

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "2.0.0-Beta3"
	kotlin("plugin.noarg") version "1.9.22"
}

group = "pl.jwizard.core"
version = "1.0.0"

var jvmVersion = JavaVersion.VERSION_17

java.sourceCompatibility = jvmVersion
java.targetCompatibility = jvmVersion

repositories {
	mavenCentral()
	maven { url = uri("https://m2.dv8tion.net/releases") }
	maven { url = uri("https://m2.chew.pro/releases") }
	maven { url = uri("https://m2.chew.pro/snapshots") }
	maven { url = uri("https://maven.lavalink.dev/snapshots") }
	maven { url = uri("https://maven.lavalink.dev/releases") }
	maven { url = uri("https://jitpack.io") }
}

noArg {
	annotation("pl.jwizard.core.config.annotation.NoArgConstructor")
}

configurations.all {
	exclude(group = "commons-logging", module = "commons-logging")
}

dependencies {
	implementation("net.dv8tion:JDA:5.0.0-beta.24")
	implementation("pw.chew:jda-chewtils:2.0-SNAPSHOT")
	implementation("dev.arbjerg:lavaplayer:2.2.1")
	implementation("dev.lavalink.youtube:v2:1.8.3")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.cloud:spring-cloud-vault-config:4.1.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
	implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.16.1")
	implementation("commons-validator:commons-validator:1.8.0")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("com.mysql:mysql-connector-j:8.4.0")
	implementation("org.apache.commons:commons-collections4:4.4")
	implementation("org.apache.commons:commons-text:1.12.0")
	implementation("commons-io:commons-io:2.16.1")
	implementation("com.google.code.gson:gson:2.11.0")
	implementation("io.github.cdimascio:dotenv-java:2.2.0")

	runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<Delete>("clean") {
	doLast {
		val binDir = file("$projectDir/.bin")
		if (binDir.exists()) {
			binDir.deleteRecursively()
		}
	}
}

tasks.withType<BootJar> {
	destinationDirectory = file("$projectDir/.bin")
	archiveFileName = "jwizard-core.jar"
}

fun getEnv(name: String, def: Any = ""): String {
	return System.getenv("JWIZARD_CORE_$name") ?: def.toString()
}

tasks.register<Copy>("createEnv") {
	val envFile = file("$projectDir/.bin/.env")
	if (envFile.exists()) {
		envFile.delete()
	}
	val currentDateTime = LocalDateTime.now()
	val values = mapOf(
		"VAULT_TOKEN" to getEnv("VAULT_TOKEN"),
		"VAULT_SERVER" to getEnv("VAULT_SERVER"),
		"BUILD_VERSION" to getEnv("BUILD_VERSION", "DEVELOPMENT"),
		"BUILD_DATE" to currentDateTime.toString(),
	)
	val str = values.entries.joinToString(separator = "\n") {
		"JWIZARD_CORE_${it.key}=${it.value}"
	}
	envFile.writeText(str)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict") // set JSR-305 annotations policy
		jvmTarget = jvmVersion.toString()
	}
}
