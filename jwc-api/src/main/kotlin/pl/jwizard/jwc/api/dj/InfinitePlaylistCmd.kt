/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command to toggle the infinite loop mode for the current playlist.
 *
 * This command enables or disables the infinite loop mode, which repeats the playlist continuously. The current loop
 * state is logged and an appropriate message is sent to the user indicating whether the playlist loop has been
 * activated or deactivated.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.INFINITE)
class InfinitePlaylistCmd(commandEnvironment: CommandEnvironmentBean) : DjCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<InfinitePlaylistCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	/**
	 * Executes the command to toggle infinite loop mode for the playlist.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val queueTrackScheduler = manager.state.queueTrackScheduler

		val isInLoop = queueTrackScheduler.audioRepeat.togglePlaylistLoop()
		log.jdaInfo(context, "Current playlist infinite playing state: %s.", isInLoop.toString())

		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = if (isInLoop) {
					I18nResponseSource.ADD_PLAYLIST_TO_INFINITE_LOOP
				} else {
					I18nResponseSource.REMOVED_PLAYLIST_FROM_INFINITE_LOOP
				},
				args = mapOf("playlistLoopCmd" to Command.INFINITE.parseWithPrefix(context.prefix)),
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
