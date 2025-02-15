package pl.jwizard.jwc.command.interaction

import pl.jwizard.jwc.core.i18n.source.I18nActionSource
import pl.jwizard.jwc.core.jda.emoji.BotEmoji

enum class InteractionButton(
	val id: String,
	val i18nSource: I18nActionSource,
	val emoji: BotEmoji? = null,
) {
	REFRESH("refresh", I18nActionSource.REFRESH, BotEmoji.REFRESH),
	FIRST("first", I18nActionSource.FIRST, BotEmoji.FIRST_PAGE),
	PREV("prev", I18nActionSource.PREV, BotEmoji.PREV_PAGE),
	NEXT("next", I18nActionSource.NEXT, BotEmoji.NEXT_PAGE),
	LAST("last", I18nActionSource.LAST, BotEmoji.LAST_PAGE),
	YES("yes", I18nActionSource.YES, BotEmoji.CHECK_YES),
	NO("no", I18nActionSource.NO, BotEmoji.CHECK_NO),
	;
}
