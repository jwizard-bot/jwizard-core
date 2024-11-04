/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.dj

import dev.arbjerg.lavalink.client.player.LavalinkPlayer
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder
import dev.arbjerg.lavalink.client.player.Track
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.mono.AsyncUpdatableHook
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.track.TrackOffsetOutOfBoundsException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.util.logger

/**
 * Command to skip to a specific track in the queue.
 *
 * This command allows skipping multiple tracks and starting playback from a specific position in the queue. It ensures
 * that the bot is in the same voice channel as the user, the bot is in playing mode, and that the queue is not empty
 * before executing the command.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.SKIPTO)
class SkipQueueToTrackCmd(
	commandEnvironment: CommandEnvironmentBean,
) : DjCommandBase(commandEnvironment), AsyncUpdatableHook<LavalinkPlayer, PlayerUpdateBuilder, Pair<Track, Int>> {

	companion object {
		private val log = logger<SkipQueueToTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val shouldPlayingMode = true
	override val queueShouldNotBeEmpty = true

	/**
	 * Executes the command to skip to a specific track in the queue.
	 *
	 * This method skips a certain number of tracks based on the position provided by the user and starts playing the
	 * selected track. If the position is out of bounds, an exception is thrown.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The music manager responsible for handling the audio queue.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val position = context.getArg<Int>(Argument.POS)

		val queue = manager.state.queueTrackScheduler.queue
		if (queue.positionIsOutOfBounds(position)) {
			throw TrackOffsetOutOfBoundsException(context, position, queue.size)
		}
		val currentTrack = queue.skipToPosition(position)!!

		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response, this)
		asyncUpdatableHandler.performAsyncUpdate(
			monoAction = manager.createdOrUpdatedPlayer.setTrack(currentTrack),
			payload = Pair(currentTrack, position),
		)
	}

	/**
	 * Handles successful asynchronous track skipping.
	 *
	 * This method is called upon successful execution of the asynchronous skip-to-track operation. It logs the skipped
	 * track count and details about the currently playing track, then creates a message to confirm the action to the user.
	 *
	 * @param context The context of the command, which includes details about user interaction.
	 * @param result The player result after the asynchronous update.
	 * @param payload A pair containing the selected track and its position in the queue.
	 * @return A [MessageEmbed] object with a confirmation message indicating the skipped tracks and the new track playing.
	 */
	override fun onAsyncSuccess(
		context: CommandContext,
		result: LavalinkPlayer,
		payload: Pair<Track, Int>,
	): MessageEmbed {
		val (currentTrack, position) = payload
		log.jdaInfo(context, "Skipped: %d tracks in queue and started playing: %s.", position, currentTrack.qualifier)
		return createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.SKIP_TO_SELECT_TRACK_POSITION,
				args = mapOf(
					"countOfSkippedTracks" to position - 1,
					"currentTrack" to currentTrack.mdTitleLink,
				)
			)
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
