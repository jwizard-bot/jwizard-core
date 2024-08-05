/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwizard")
data class BotProperties(
	var deployment: DeploymentProperties = DeploymentProperties(),
	var appName: String = "JWizard",
	var appIconPath: String = "",
	var instance: InstanceProperties = InstanceProperties(),
	var apiHost: String = "",
	var defaultActivity: String = "",
	var splashes: SplashesProperties = SplashesProperties(),
	var pagination: PaginationProperties = PaginationProperties(),
	val cdn: CdnProperties = CdnProperties(),
)
