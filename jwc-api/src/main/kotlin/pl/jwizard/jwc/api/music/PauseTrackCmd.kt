/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.music

import dev.arbjerg.lavalink.client.player.LavalinkPlayer
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.async.AsyncUpdatableHook
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.PercentageIndicatorBar
import pl.jwizard.jwc.core.util.ext.duration
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.ext.thumbnailUrl
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.millisToDTF
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command for pausing the currently playing track.
 *
 * This command pauses the current track and provides detailed feedback to the user, including track information
 * and progress through the track at the time of pausing. It also supports async updates through a hook system.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.PAUSE)
class PauseTrackCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment), AsyncUpdatableHook<LavalinkPlayer, PlayerUpdateBuilder, MusicManager> {

	companion object {
		private val log = logger<PauseTrackCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val shouldBeContentSenderOrSuperuser = true

	/**
	 * Executes the pause command.
	 *
	 * This method pauses the currently playing track using the music manager and initiates an asynchronous update to
	 * reflect the change. The result is sent to the user through the provided response object.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeMusic(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response, this)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.setPaused(true),
			payload = manager,
		)
	}

	/**
	 * Called when the async pause operation succeeds.
	 *
	 * This method generates a detailed response to the user, including the track's progress, total duration, and a
	 * percentage bar representing the current position within the track. The response is embedded as a rich message with
	 * visual elements.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param result The result of the async operation, in this case, the updated [LavalinkPlayer].
	 * @param payload The music manager used for handling audio playback and queue management.
	 * @return A MessageEmbed containing detailed information about the paused track.
	 * @throws UnexpectedException If the paused track is not found.
	 */
	override fun onAsyncSuccess(context: CommandContext, result: LavalinkPlayer, payload: MusicManager): MessageEmbed {
		val pausedTrack = payload.cachedPlayer?.track ?: throw UnexpectedException(context, "Paused track is NULL.")
		val elapsedTime = payload.cachedPlayer?.position ?: 0

		log.jdaInfo(context, "Current playing track: %s was paused.", pausedTrack.qualifier)

		val percentageIndicatorBar = PercentageIndicatorBar(
			start = elapsedTime,
			total = pausedTrack.duration,
		)
		val messageBuilder = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.PAUSED_TRACK,
				args = mapOf(
					"track" to pausedTrack.mdTitleLink,
					"resumeCmd" to Command.RESUME.parseWithPrefix(context.prefix),
				),
			)
			.setValueField(percentageIndicatorBar.generateBar(), inline = false)
			.setKeyValueField(I18nAudioSource.PAUSED_TRACK_TIME, millisToDTF(elapsedTime))

		pausedTrack.let {
			messageBuilder.setKeyValueField(
				I18nAudioSource.PAUSED_TRACK_ESTIMATE_TIME,
				millisToDTF(it.duration - elapsedTime),
			)
			messageBuilder.setKeyValueField(I18nAudioSource.PAUSED_TRACK_TOTAL_DURATION, millisToDTF(it.duration))
		}
		messageBuilder
			.setColor(JdaColor.PRIMARY)
			.setArtwork(pausedTrack.thumbnailUrl)

		return messageBuilder.build()
	}
}
