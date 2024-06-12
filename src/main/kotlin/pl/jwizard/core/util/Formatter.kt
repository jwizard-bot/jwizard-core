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
import pl.jwizard.core.command.reflect.CommandDetailsDto
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nMiscLocale
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

	fun createTrackCurrentAndMaxDuration(audioTrack: ExtendedAudioTrackInfo) = "%s / %s".format(
		DateUtils.convertMilisToDTF(audioTrack.timestamp),
		DateUtils.convertMilisToDTF(audioTrack.maxDuration)
	)

	fun createRichPageableTrackInfo(index: Int, audioTrack: AudioTrack): String = "`%d` [ %s ] %s\n**%s**".format(
		index + 1,
		DateUtils.convertMilisToDTF(audioTrack.duration),
		(audioTrack.userData as Member).user.asTag,
		createRichTrackTitle(audioTrack.info),
	)

	fun createPercentageRepresentation(audioTrack: ExtendedAudioTrackInfo): String {
		val progressPerc = audioTrack.timestamp.toDouble() / audioTrack.maxDuration.toDouble() * 100f
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
		stringJoiner.add("\t`${prefix}${commandName} ${BotUtils.getLang(lang, command.argsDesc)}`")
		stringJoiner.add("\n\t`${prefix}${command.alias} ${BotUtils.getLang(lang, command.argsDesc)}`")
		return stringJoiner.toString()
	}

	fun trackStr(audioTrack: AudioTrack): String = audioTrack.info.title

	fun boolStr(value: Boolean) = if (value) "ON" else "OFF"

	fun guildTag(guild: Guild?) = "${guild?.name}(ID:${guild?.id})"

	fun toStateTag(state: Boolean): I18nLocale = if (state) I18nMiscLocale.TURN_ON else I18nMiscLocale.TURN_OFF
}
