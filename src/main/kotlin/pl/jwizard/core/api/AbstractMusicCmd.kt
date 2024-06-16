/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import pl.jwizard.core.audio.AudioPlayerSendHandler
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CommandModule
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.exception.CommandException
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.util.Formatter

abstract class AbstractMusicCmd(
	botConfiguration: BotConfiguration,
	protected val playerManager: PlayerManager,
) : AbstractAudioCmd(
	botConfiguration
) {
	protected var inPlayingMode = false // invoke only, when bot playing audio
	protected var inIdleMode = false // invoke also in idle mode (without playing audio)
	protected var isPaused = false // invoke only, when current playing audio is paused

	override fun executeAudioCmd(sendHandler: AudioPlayerSendHandler?, event: CompoundCommandEvent) {
		checkIfCommandModuleIsEnabled(event, CommandModule.MUSIC)
		val musicManager = playerManager.findMusicManager(event)
		// check, if command is available only for discrete audio source
		if (musicManager.actions.radioStationDto != null) {
			throw CommandException.CommandAvailableOnlyForDiscreteTrackException(event)
		}
		if (inPlayingMode && (sendHandler?.isInPlayingMode() == false
				|| musicManager.audioPlayer.isPaused) && !isPaused
		) {
			throw AudioPlayerException.ActiveMusicPlayingNotFoundException(event)
		}
		if (!inIdleMode) {
			if (isPaused) {
				musicManager.actions.getPausedTrackInfo()
			}
			val userVoiceState = validateUserVoiceState(event)
			if (checkIfUserIsWithBotOnAudioChannel(userVoiceState, event)) {
				joinToVoiceAndOpenAudioConnection(event)
			}
		}
		executeMusicCmd(event)
	}

	protected fun createDetailedTrackEmbedMessage(
		event: CompoundCommandEvent,
		i18nDescription: I18nLocale,
		i18nTimestampText: I18nLocale,
		track: ExtendedAudioTrackInfo,
	): MessageEmbed = CustomEmbedBuilder(botConfiguration, event)
		.addAuthor(track.sender)
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
