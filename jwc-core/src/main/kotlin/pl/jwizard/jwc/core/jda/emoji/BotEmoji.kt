/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.emoji

import net.dv8tion.jda.api.entities.emoji.Emoji

/**
 * Enum representing custom emojis used by the bot. Each emoji has a [displayName], which is used to fetch or represent
 * it in Discord.
 *
 * @property displayName The name of the emoji, used as an identifier and in its formatted representation.
 * @author Miłosz Gilga
 */
enum class BotEmoji(val displayName: String) {
	PREV_PAGE("prev_page"),
	NEXT_PAGE("next_page"),
	FIRST_PAGE("first_page"),
	LAST_PAGE("last_page"),
	REFRESH("refresh"),
	CHECK_YES("check_yes"),
	CHECK_NO("check_no"),
	WEBSITE("website"),
	;

	/**
	 * Converts this bot emoji into a JDA [Emoji] object using the provided cache. If the emoji is found in the cache, it
	 * is formatted and converted into an [Emoji] instance.
	 *
	 * @param botEmojisCache The cache containing mappings of emoji names to their Discord IDs.
	 * @return The `Emoji` object for the bot's custom emoji, or `null` if the emoji is not found in the cache.
	 */
	fun toEmoji(botEmojisCache: BotEmojisCacheBean) = toFormatted(botEmojisCache)?.let { Emoji.fromFormatted(it) }

	/**
	 * Formats this bot emoji into its Discord representation using the provided cache.
	 *
	 * The formatted representation is in the form `<:name:id>`, where `name` is the [displayName] and `id` is the
	 * unique Discord identifier for the emoji.
	 *
	 * @param botEmojisCache The cache containing mappings of emoji names to their Discord IDs.
	 * @return The formatted string representing the emoji, or `null` if the emoji is not found in the cache.
	 */
	fun toFormatted(botEmojisCache: BotEmojisCacheBean): String? {
		val emojis = botEmojisCache.emojis
		if (!emojis.containsKey(displayName)) {
			return null
		}
		val emojiId = emojis[displayName]
		return "<:${displayName}:${emojiId}>"
	}
}
