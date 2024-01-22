/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

var jvmVersion = JavaVersion.VERSION_17

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "2.0.0-Beta3"
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
}

dependencies {
	implementation("net.dv8tion:JDA:4.4.1_353")
	implementation("dev.arbjerg:lavaplayer:2.1.0")
	implementation("org.slf4j:slf4j-api:2.0.11")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("com.squareup.okhttp3:okhttp:4.12.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.9.22")

	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = jvmVersion.toString()
	}
}
