/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.core.audio.AudioPlayerSendHandler
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.CommandModule
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.exception.CommandException
import pl.jwizard.core.exception.UserException
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.util.BotUtils
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.util.Formatter

abstract class AbstractMusicCmd(
	botConfiguration: BotConfiguration,
	protected val playerManagerFacade: PlayerManagerFacade,
) : AbstractCompositeCmd(
	botConfiguration
) {
	protected var inPlayingMode = false // invoke only, when bot playing audio
	protected var inIdleMode = false // invoke also in idle mode (without playing audio)
	protected var onSameChannelWithBot = false // invoke only when member is together with bot on channel
	protected var selfJoinable = false // declared for bot auto-join on channel
	protected var isPaused = false // invoke only, when current playing audio is paused

	override fun execute(event: CompoundCommandEvent) {
		checkIfCommandModuleIsEnabled(event, CommandModule.MUSIC)
		val musicTextChannelId = guildSettings.getGuildProperties(event.guildId).musicTextChannelId
		if (musicTextChannelId != null && event.textChannel.id != musicTextChannelId) {
			val sendingChannel = event.guild?.getTextChannelById(musicTextChannelId)
			throw AudioPlayerException.ForbiddenTextChannelException(event, sendingChannel?.name ?: "")
		}
		val audioSendHandler = event.guild?.audioManager?.sendingHandler as AudioPlayerSendHandler?
		val guildVoiceState = event.botMember?.voiceState
		if (guildVoiceState == null || guildVoiceState.isMuted) {
			throw AudioPlayerException.LockCommandOnTemporaryHaltedException(event)
		}
		val musicManager = playerManagerFacade.findMusicManager(event)
		if (inPlayingMode && (audioSendHandler?.isInPlayingMode() == false
				|| musicManager.audioPlayer.isPaused) && !isPaused
		) {
			throw AudioPlayerException.ActiveMusicPlayingNotFoundException(event)
		}
		if (!inIdleMode) {
			if (isPaused) {
				musicManager.actions.getPausedTrackInfo()
			}
			val userVoiceState = event.member.voiceState
			if (userVoiceState == null || !userVoiceState.inVoiceChannel() || userVoiceState.isDeafened) {
				throw UserException.UserOnVoiceChannelNotFoundException(event)
			}
			val afkChannel = event.guild?.afkChannel
			if (afkChannel != null && afkChannel == userVoiceState.channel) {
				throw CommandException.UsedCommandOnForbiddenChannelException(event)
			}
			val botVoiceState = event.botMember.voiceState
			if (selfJoinable && botVoiceState?.inVoiceChannel() == false) {
				event.guild?.audioManager?.openAudioConnection(userVoiceState.channel)
			} else {
				val (isNotOwner, isNotManager) = BotUtils.validateUserDetails(guildSettings, event)
				if (botVoiceState?.channel != userVoiceState.channel && onSameChannelWithBot
					&& (isNotOwner || isNotManager)
				) {
					throw UserException.UserOnVoiceChannelWithBotNotFoundException(event)
				}
			}
		}
		executeMusicCmd(event)
	}

	protected fun createDetailedTrackEmbedMessage(
		event: CompoundCommandEvent,
		i18nDescription: I18nLocale,
		i18nTimestampText: I18nLocale,
		track: ExtendedAudioTrackInfo,
	): MessageEmbed = CustomEmbedBuilder(event, botConfiguration)
		.addAuthor()
		.addDescription(i18nDescription)
		.appendKeyValueField(I18nMiscLocale.TRACK_NAME, Formatter.createRichTrackTitle(track))
		.addSpace()
		.appendKeyValueField(I18nMiscLocale.TRACK_ADDDED_BY, track.sender.asTag)
		.appendValueField(Formatter.createPercentageRepresentation(track), false)
		.appendKeyValueField(i18nTimestampText, Formatter.createTrackCurrentAndMaxDuration(track))
		.addSpace()
		.appendKeyValueField(
			I18nMiscLocale.CURRENT_TRACK_LEFT_TO_NEXT,
			DateUtils.convertMilisToDTF(track.approximateTime)
		)
		.addThumbnail(track.artworkUrl)
		.addColor(EmbedColor.WHITE)
		.build()

	abstract fun executeMusicCmd(event: CompoundCommandEvent)
}
