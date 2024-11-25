/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command to clear the repeat mode for the current track.
 *
 * This command disables the repeat mode for the currently playing track, stopping it from repeating multiple times.
 * The bot needs to be in the same voice channel as the user for this command to be executed.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.REPEATCLS)
class ClearRepeatTrackCmd(commandEnvironment: CommandEnvironmentBean) : MusicCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<ClearRepeatTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true

	/**
	 * Executes the command to disable the repeat mode for the current track.
	 *
	 * This method sets the repeat count of the currently playing track to zero, effectively removing repeat mode. It
	 * logs the action and sends an embed message to the user to confirm that repeat mode has been disabled.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeMusic(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		manager.state.queueTrackScheduler.updateCountOfRepeats(0)

		val currentPlayingTrack = manager.cachedPlayer?.track
		log.jdaInfo(context, "Repeating of current playing track: %s was removed.", currentPlayingTrack?.qualifier)

		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.REMOVE_MULTIPLE_REPEATING_TRACK,
				args = mapOf(
					"track" to currentPlayingTrack?.mdTitleLink,
					"repeatingCmd" to Command.REPEAT.parseWithPrefix(context.prefix),
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
