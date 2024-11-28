/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction

import pl.jwizard.jwc.core.i18n.source.I18nActionSource
import pl.jwizard.jwc.core.jda.emoji.BotEmoji

/**
 * Enum representing the interaction buttons used in the application. Each button is associated with a unique
 * identifier and a corresponding internationalization source.
 *
 * @property id A unique string identifier for the button, used to differentiate between different buttons in the UI.
 * @property i18nSource The source for internationalization, providing localized text for the button's action.
 * @property emoji An optional emoji associated with the button.
 * @author Miłosz Gilga
 */
enum class InteractionButton(
	val id: String,
	val i18nSource: I18nActionSource,
	val emoji: BotEmoji? = null,
) {

	/**
	 * The button that refreshes the current view or data.
	 */
	REFRESH("refresh", I18nActionSource.REFRESH, BotEmoji.REFRESH),

	/**
	 * The button that navigates to the first page or item.
	 */
	FIRST("first", I18nActionSource.FIRST, BotEmoji.FIRST_PAGE),

	/**
	 * The button that navigates to the previous page or item.
	 */
	PREV("prev", I18nActionSource.PREV, BotEmoji.PREV_PAGE),

	/**
	 * The button that navigates to the next page or item.
	 */
	NEXT("next", I18nActionSource.NEXT, BotEmoji.NEXT_PAGE),

	/**
	 * The button that navigates to the last page or item.
	 */
	LAST("last", I18nActionSource.LAST, BotEmoji.LAST_PAGE),

	/**
	 * The button that set vote in poll to YES.
	 */
	YES("yes", I18nActionSource.YES, BotEmoji.CHECK_YES),

	/**
	 * The button that set vote in poll to NO.
	 */
	NO("no", I18nActionSource.NO, BotEmoji.CHECK_NO),
	;
}
