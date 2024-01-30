/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.http

import com.fasterxml.jackson.annotation.JsonCreator

data class TokenResDto(
	val accessToken: String,
	val refreshToken: String
) {
	@JsonCreator
	constructor() : this("", "")
}
