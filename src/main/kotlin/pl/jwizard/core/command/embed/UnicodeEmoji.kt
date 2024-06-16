/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.embed

import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.entities.emoji.EmojiUnion

// https://unicode.org/emoji/charts/full-emoji-list.html#1f44d

enum class UnicodeEmoji(
	val code: String,
	val index: Int,
) {
	THUMBS_UP("\uD83D\uDC4D", 0),
	THUMBS_DOWN("\uD83D\uDC4E", 0),

	NUMBER_ZERO("\u0030\u20E3", 0),
	NUMBER_ONE("\u0031\u20E3", 1),
	NUMBER_TWO("\u0032\u20E3", 2),
	NUMBER_THREE("\u0033\u20E3", 3),
	NUMBER_FOUR("\u0034\u20E3", 4),
	NUMBER_FIVE("\u0035\u20E3", 5),
	NUMBER_SIX("\u0036\u20E3", 6),
	NUMBER_SEVEN("\u0037\u20E3", 7),
	NUMBER_EIGHT("\u0038\u20E3", 8),
	NUMBER_NINE("\u0039\u20E3", 9),
	;

	fun checkEquals(emoji: EmojiUnion): Boolean = code == emoji.asReactionCode

	fun createEmoji(): Emoji = Emoji.fromUnicode(code)

	companion object {
		fun getNumbers(maxNumber: Int): List<UnicodeEmoji> = entries
			.toTypedArray()
			.filter { it.code.contains("\u20E3") }
			.take(maxNumber)
	}
}
