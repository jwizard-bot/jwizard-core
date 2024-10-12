/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction

import pl.jwizard.jwc.core.i18n.source.I18nActionSource

/**
 * Enum representing the interaction buttons used in the application. Each button is associated with a unique
 * identifier and a corresponding internationalization source.
 *
 * @property id A unique string identifier for the button, used to differentiate between different buttons in the UI.
 * @property i18nSource The source for internationalization, providing localized text for the button's action.
 * @author Miłosz Gilga
 */
enum class InteractionButton(
	val id: String,
	val i18nSource: I18nActionSource,
) {

	/**
	 * The button that refreshes the current view or data.
	 */
	REFRESH("refresh", I18nActionSource.REFRESH),

	/**
	 * The button that navigates to the first page or item.
	 */
	FIRST("first", I18nActionSource.FIRST),

	/**
	 * The button that navigates to the previous page or item.
	 */
	PREV("prev", I18nActionSource.PREV),

	/**
	 * The button that navigates to the next page or item.
	 */
	NEXT("next", I18nActionSource.NEXT),

	/**
	 * The button that navigates to the last page or item.
	 */
	LAST("last", I18nActionSource.LAST),
	;
}
