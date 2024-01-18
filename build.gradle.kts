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

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.slf4j:slf4j-api:2.0.11")

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
