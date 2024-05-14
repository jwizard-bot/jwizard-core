/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
@ConfigurationProperties(prefix = "jwizard")
data class BotProperties(
	var deployment: DeploymentProperties = DeploymentProperties(),
	var appName: String = "JWizard",
	var appIconPath: Resource? = null,
	var instance: InstanceProperties = InstanceProperties(),
	var apiHost: String = "",
	var defaultActivity: String = "",
	var splashes: SplashesProperties = SplashesProperties(),
	var pagination: PaginationProperties = PaginationProperties(),
)

data class DeploymentProperties(
	var buildVersion: String = "UNKNOWN",
	var lastBuildDate: String = "UNKNOWN",
)

data class InstanceProperties(
	var authToken: String = "",
	var appId: String = "",
)

data class SplashesProperties(
	var enabled: Boolean = true,
	var intervalSec: Long = 5,
	var list: List<String> = emptyList(),
)

data class PaginationProperties(
	var maxElementsPerPage: Int = 20,
	var menuAliveSec: Long = 60
)