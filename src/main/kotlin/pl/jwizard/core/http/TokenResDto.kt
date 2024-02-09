/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.http

import pl.jwizard.core.config.annotation.NoArgConstructor

@NoArgConstructor
data class TokenResDto(
	val accessToken: String,
	val refreshToken: String
)
