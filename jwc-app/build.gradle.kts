plugins {
	alias(libs.plugins.shadowJar)
}

dependencies {
	rootProject.subprojects
		.filter { it.name != project.name }
		.forEach { implementation(project(":${it.name}")) }
}

tasks {
	jar {
		manifest {
			attributes(
				"Main-Class" to "pl.jwizard.jwc.app.JWizardCoreEntrypointKt"
			)
		}
	}

	shadowJar {
		archiveBaseName.set("jwizard-core")
		archiveClassifier.set("")
		archiveVersion.set("")
		destinationDirectory.set(file("$rootDir/.bin"))
	}
}
