/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.ext.thumbnailUrl
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.logger

/**
 * Command to toggle infinite loop on the currently playing track.
 *
 * This command enables or disables the infinite loop mode for the track that is currently playing. It requires the user
 * to be in the same voice channel as the bot and to either be the track's original requester or have superuser
 * permissions.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.LOOP)
class LoopTrackCmd(commandEnvironment: CommandEnvironmentBean) : MusicCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<LoopTrackCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val shouldBeContentSenderOrSuperuser = true

	/**
	 * Toggles the infinite loop for the currently playing track.
	 *
	 * This method either enables or disables the infinite loop mode for the currently playing track. It responds with a
	 * confirmation message in the form of an embed, indicating the current loop status.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeMusic(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val isInLoop = manager.state.queueTrackScheduler.audioRepeat.toggleTrackLoop()
		val currentPlayingTrack = manager.cachedPlayer?.track
		log.jdaInfo(
			context,
			"Current infinite playing state: %s for track: %s.",
			isInLoop.toString(),
			currentPlayingTrack?.qualifier
		)
		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = if (isInLoop) {
					I18nResponseSource.ADD_TRACK_TO_INFINITE_LOOP
				} else {
					I18nResponseSource.REMOVED_TRACK_FROM_INFINITE_LOOP
				},
				args = mapOf(
					"track" to currentPlayingTrack?.mdTitleLink,
					"loopCmd" to Command.LOOP.parseWithPrefix(context),
				),
			)
			.setArtwork(currentPlayingTrack?.thumbnailUrl)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
