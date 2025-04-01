dependencies {
	implementation(libs.jda)
	implementation(libs.reactor)

	implementation(project(":jwc-core"))
	implementation(project(":jwc-exception"))
	implementation(project(":jwc-radio"))
}
