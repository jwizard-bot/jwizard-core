package pl.jwizard.jwc.core.jda.emoji

import net.dv8tion.jda.api.entities.emoji.Emoji

enum class BotEmoji(val displayName: String) {
	PREV_PAGE("prev_page"),
	NEXT_PAGE("next_page"),
	FIRST_PAGE("first_page"),
	LAST_PAGE("last_page"),
	REFRESH("refresh"),
	CHECK_YES("check_yes"),
	CHECK_NO("check_no"),
	;

	fun toEmoji(
		botEmojisCache: BotEmojisCacheBean,
	) = toFormatted(botEmojisCache)?.let { Emoji.fromFormatted(it) }

	private fun toFormatted(botEmojisCache: BotEmojisCacheBean): String? {
		val emojis = botEmojisCache.emojis
		if (!emojis.containsKey(displayName)) {
			return null
		}
		val emojiId = emojis[displayName]
		return "<:${displayName}:${emojiId}>"
	}
}
