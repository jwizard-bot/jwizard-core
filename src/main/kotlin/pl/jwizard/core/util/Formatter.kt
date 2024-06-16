/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.util

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import org.apache.commons.lang3.StringUtils
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.command.reflect.CommandArgOptionDto
import pl.jwizard.core.command.reflect.CommandDetailsDto
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nMiscLocale
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

object Formatter {
	private const val PLAYER_INDICATOR_FULL = '█'
	private const val PLAYER_INDICATOR_EMPTY = '▒'
	private const val MAX_EMBED_PLAYER_INDICATOR_LENGTH = 36

	fun createRichTrackTitle(audioTrackInfo: AudioTrackInfo): String = "[%s](%s)".format(
		audioTrackInfo.title.replace("*", ""),
		audioTrackInfo.uri,
	)

	fun createRichTrackTitle(audioTrack: AudioTrack) = "[ %s ] : [%s](%s)".format(
		DateUtils.convertMilisToDTF(audioTrack.duration),
		audioTrack.info.title.replace("*", ""),
		audioTrack.info.uri,
	)

	fun createYoutubeRedirectSearchLink(title: String, author: String): String {
		// remove all spaces to plus characters and encode to right URI syntax
		val trackUri = URLEncoder.encode("$title $author", StandardCharsets.UTF_8)
		return "[%s](%s)".format(
			"$title - $author",
			"https://www.youtube.com/results?search_query=${trackUri.replace("%20", "+")}"
		)
	}

	fun createTrackCurrentAndMaxDuration(audioTrack: ExtendedAudioTrackInfo) = "%s / %s".format(
		DateUtils.convertMilisToDTF(audioTrack.timestamp),
		DateUtils.convertMilisToDTF(audioTrack.maxDuration)
	)

	fun createRichPageableTrackInfo(index: Int, audioTrack: AudioTrack): String = "`%d` [ %s ] %s\n**%s**".format(
		index + 1,
		DateUtils.convertMilisToDTF(audioTrack.duration),
		(audioTrack.userData as Member).user.name,
		createRichTrackTitle(audioTrack.info),
	)

	fun createPercentageRepresentation(audioTrack: ExtendedAudioTrackInfo): String =
		createPercentageRepresentation(audioTrack.timestamp, audioTrack.maxDuration)

	fun createPercentageRepresentation(start: Long, maxDuration: Long): String {
		val progressPerc = start.toDouble() / maxDuration.toDouble() * 100f
		val blocksCount = Math.round(MAX_EMBED_PLAYER_INDICATOR_LENGTH * progressPerc / 100).toInt()
		val emptyBlocksCount = MAX_EMBED_PLAYER_INDICATOR_LENGTH - blocksCount
		val formatToBlocks: (count: Int, character: Char) -> String = { count, character ->
			character.toString().repeat(0.coerceAtLeast(count))
		}
		return formatToBlocks(blocksCount, PLAYER_INDICATOR_FULL) +
			formatToBlocks(emptyBlocksCount, PLAYER_INDICATOR_EMPTY)
	}

	fun createCommandSyntax(
		commandName: String,
		command: CommandDetailsDto,
		legacyPrefix: String,
		lang: String,
	): String {
		val prefix = legacyPrefix.ifEmpty { "/" }
		val stringJoiner = StringJoiner(StringUtils.EMPTY)
		stringJoiner.add("\n\n")
		stringJoiner.add("* `${prefix}${commandName} |${BotUtils.getLang(lang, command.argsDesc)}|`\n")
		stringJoiner.add("* `${prefix}${command.alias} |${BotUtils.getLang(lang, command.argsDesc)}|`")
		return stringJoiner.toString()
	}

	fun createArgumentOptionsSyntax(options: List<CommandArgOptionDto>, lang: String): String {
		val stringJoiner = StringJoiner(StringUtils.EMPTY)
		var i = 0
		stringJoiner.add("\n\n")
		for (option in options) {
			stringJoiner.add("* `${option.rawValue}` - ${BotUtils.getLang(lang, option.desc)}")
			if (i++ < options.size && options.size != 1) {
				stringJoiner.add("\n")
			}
		}
		return stringJoiner.toString()
	}

	fun trackStr(audioTrack: AudioTrack): String = audioTrack.info.title

	fun boolStr(value: Boolean) = if (value) "ON" else "OFF"

	fun guildTag(guild: Guild?) = "${guild?.name}(ID:${guild?.id})"

	fun toStateTag(state: Boolean): I18nLocale = if (state) I18nMiscLocale.TURN_ON else I18nMiscLocale.TURN_OFF
}
