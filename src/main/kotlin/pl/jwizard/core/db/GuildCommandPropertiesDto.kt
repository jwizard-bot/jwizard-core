/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db

data class GuildCommandPropertiesDto(
	val id: Long,
	val lang: String,
	val prefix: String,
	val slashEnabled: Boolean,
	val djRoleName: String,
)
