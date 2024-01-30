/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwizard")
data class BotProperties(
	var instance: InstanceProperties? = null,
	var apiHost: String = "",
	var defaultActivity: String = "",
	var splashes: SplashesProperties = SplashesProperties(),
)

data class InstanceProperties(
	var authToken: String? = "",
	var appId: String? = "",
)

data class SplashesProperties(
	var enabled: Boolean = true,
	var intervalSec: Long = 5,
	var list: List<String> = emptyList(),
)
