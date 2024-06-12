/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot.properties

data class PaginationProperties(
	var maxElementsPerPage: Int = 20,
	var menuAliveSec: Long = 60
)
