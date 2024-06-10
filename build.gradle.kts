/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

var jvmVersion = JavaVersion.VERSION_17

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "2.0.0-Beta3"
	kotlin("plugin.noarg") version "1.9.22"
}

group = "pl.jwizard.core"
version = "1.0.0"

java.sourceCompatibility = jvmVersion
java.targetCompatibility = jvmVersion

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://m2.dv8tion.net/releases") }
	maven { url = uri("https://m2.chew.pro/releases") }
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
	implementation("net.dv8tion:JDA:4.4.1_353")
	implementation("pw.chew:jda-chewtils:1.24.1")
	implementation("dev.arbjerg:lavaplayer:2.1.2")
	implementation("dev.lavalink.youtube:v2:1.2.0")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.cloud:spring-cloud-vault-config:4.1.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
	implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.16.1")
	implementation("commons-validator:commons-validator:1.8.0")
	runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<BootJar> {
	archiveFileName = "jwizard-core.jar"
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict") // set JSR-305 annotations policy
		jvmTarget = jvmVersion.toString()
	}
}
