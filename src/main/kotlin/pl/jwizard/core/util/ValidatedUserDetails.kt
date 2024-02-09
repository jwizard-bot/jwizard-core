/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.util

data class ValidatedUserDetails(
	val isNotOwner: Boolean,
	val isNotManager: Boolean,
	val isNotDj: Boolean,
) {
	fun concatLogicOr() = !isNotOwner || !isNotManager || !isNotDj

	fun concatPositive() = isNotOwner && isNotManager && isNotDj
}
