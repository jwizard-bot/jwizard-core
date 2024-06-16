/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import net.dv8tion.jda.api.entities.GuildVoiceState
import net.dv8tion.jda.api.entities.channel.ChannelType
import org.apache.commons.lang3.StringUtils
import pl.jwizard.core.audio.AudioPlayerSendHandler
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.db.GuildDbProperty
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.exception.CommandException
import pl.jwizard.core.exception.UserException
import pl.jwizard.core.util.BotUtils

abstract class AbstractAudioCmd(
	botConfiguration: BotConfiguration,
) : AbstractCompositeCmd(
	botConfiguration,
) {
	protected var onSameChannelWithBot = false // invoke only when member is together with bot on channel
	protected var selfJoinable = false // declared for bot auto-join on channel

	override fun execute(event: CompoundCommandEvent) {
		val musicTextChannelId = guildSettings
			.fetchDbProperty(GuildDbProperty.MUSIC_TEXT_CHANNEL_ID, event.guildId, String::class)
		if (musicTextChannelId.isNotEmpty() && event.textChannel.id != musicTextChannelId) {
			val sendingChannel = event.guild?.getTextChannelById(musicTextChannelId)
			throw AudioPlayerException.ForbiddenTextChannelException(event, sendingChannel?.name ?: StringUtils.EMPTY)
		}
		val audioSendHandler = event.guild?.audioManager?.sendingHandler as AudioPlayerSendHandler?
		val guildVoiceState = event.botMember?.voiceState
		if (guildVoiceState == null || guildVoiceState.isMuted) {
			throw AudioPlayerException.LockCommandOnTemporaryHaltedException(event)
		}
		executeAudioCmd(audioSendHandler, event)
	}

	protected fun validateUserVoiceState(event: CompoundCommandEvent): GuildVoiceState {
		val userVoiceState = event.member.voiceState
		if (userVoiceState == null || userVoiceState.channel?.type != ChannelType.VOICE) {
			throw UserException.UserOnVoiceChannelNotFoundException(event)
		}
		val afkChannel = event.guild?.afkChannel
		if (afkChannel != null && afkChannel == userVoiceState.channel) {
			throw CommandException.UsedCommandOnForbiddenChannelException(event)
		}
		return userVoiceState
	}

	protected fun checkIfUserIsWithBotOnAudioChannel(
		userVoiceState: GuildVoiceState,
		event: CompoundCommandEvent,
	): Boolean {
		val botVoiceState = event.botMember?.voiceState
		if (selfJoinable && botVoiceState?.member?.voiceState?.inAudioChannel() == false) {
			return true
		}
		val (isNotOwner, isNotManager) = BotUtils.validateUserDetails(event)
		if (botVoiceState?.channel != userVoiceState.channel && onSameChannelWithBot
			&& (isNotOwner || isNotManager)
		) {
			throw UserException.UserOnVoiceChannelWithBotNotFoundException(event)
		}
		return false
	}

	protected fun joinToVoiceAndOpenAudioConnection(event: CompoundCommandEvent) =
		event.guild?.audioManager?.openAudioConnection(event.member.voiceState?.channel)


	protected abstract fun executeAudioCmd(sendHandler: AudioPlayerSendHandler?, event: CompoundCommandEvent)
}
