/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot.properties

data class SplashesProperties(
	var enabled: Boolean = true,
	var intervalSec: Long = 5,
	var list: List<String> = emptyList(),
)
