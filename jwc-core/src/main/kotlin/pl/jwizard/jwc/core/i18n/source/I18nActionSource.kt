/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource

/**
 * Provides internationalization (i18n) placeholders for various action-related messages.
 *
 * @property placeholder The placeholder key used to retrieve the localized message.
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 * @see I18nBean
 */
enum class I18nActionSource(private val placeholder: String) : I18nLocaleSource {
	REFRESH_BUTTON("jwc.action.refreshButton"),
	;

	override fun getPlaceholder() = placeholder
}
