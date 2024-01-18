/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
plugins {
	kotlin("jvm") version "1.9.21"
}

group = "pl.jwizard.core"
version = "1.0.0"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(17)
}