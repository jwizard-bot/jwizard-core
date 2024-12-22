/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler

import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Enum representing mappings for player-friendly exception messages.
 *
 * Each enum constant is associated with a specific string pattern and an internationalization source for providing
 * localized exception messages.
 *
 * @property stringPattern A part of the exception message to identify the error (nullable).
 * @property i18nLocaleSource An internationalization source used to retrieve localized messages.
 * @author Miłosz Gilga
 */
enum class PlayerFriendlyExceptionMapper(
	val stringPattern: String?,
	val i18nLocaleSource: I18nExceptionSource,
) {
	BLOCKED_IN_COUNTRY("who has blocked it in your country", I18nExceptionSource.TRACK_IS_BLOCKED_IN_COUNTRY),
	GENERAL(null, I18nExceptionSource.ISSUE_WHILE_LOADING_TRACK),
	;
}
