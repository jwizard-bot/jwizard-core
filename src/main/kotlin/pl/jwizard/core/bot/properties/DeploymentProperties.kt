/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot.properties

data class DeploymentProperties(
	var buildVersion: String = "UNKNOWN",
	var lastBuildDate: String = "UNKNOWN",
)
