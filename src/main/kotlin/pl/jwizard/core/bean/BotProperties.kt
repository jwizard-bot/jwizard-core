/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bean

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwizard")
data class BotProperties(
	var instance: InstanceProperties? = null,
	var apiHost: String = "",
)

data class InstanceProperties(
	var authToken: String? = "",
	var appId: String? = "",
)
