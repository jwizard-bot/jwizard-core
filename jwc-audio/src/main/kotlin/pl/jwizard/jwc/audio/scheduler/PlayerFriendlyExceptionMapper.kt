package pl.jwizard.jwc.audio.scheduler

import pl.jwizard.jwl.i18n.source.I18nExceptionSource

internal enum class PlayerFriendlyExceptionMapper(
	val stringPattern: String?,
	val i18nLocaleSource: I18nExceptionSource,
) {
	BLOCKED_IN_COUNTRY(
		"who has blocked it in your country",
		I18nExceptionSource.TRACK_IS_BLOCKED_IN_COUNTRY,
	),
	AGE_RESTRICTED("this content is age-restricted", I18nExceptionSource.TRACK_IS_AGE_RESTRICTED),
	GENERAL(null, I18nExceptionSource.ISSUE_WHILE_LOADING_TRACK),
	;
}
