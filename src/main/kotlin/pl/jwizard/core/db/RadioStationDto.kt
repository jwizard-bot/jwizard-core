/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db

data class RadioStationDto(
	val name: String,
	val slug: String,
	val streamUrl: String,
	val proxyStreamUrl: String?,
	val coverImage: String?,
)
