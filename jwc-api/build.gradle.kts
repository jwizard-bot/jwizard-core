dependencies {
	implementation(libs.jda)
	implementation(libs.lavalinkProtocol)
	implementation(libs.reactor)

	implementation(project(":jwc-audio"))
	implementation(project(":jwc-audio-gateway"))
	implementation(project(":jwc-command"))
	implementation(project(":jwc-core"))
	implementation(project(":jwc-exception"))
	implementation(project(":jwc-radio"))
	implementation(project(":jwc-vote"))
}
