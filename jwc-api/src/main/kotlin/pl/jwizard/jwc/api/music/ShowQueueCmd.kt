/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.music

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.util.ext.name
import pl.jwizard.jwc.core.util.mdBold
import pl.jwizard.jwc.core.util.mdCode
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwc.core.util.millisToDTF
import pl.jwizard.jwl.command.Command
import java.util.*

/**
 * Command that shows the current music queue for the guild.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.QUEUE_SHOW)
class ShowQueueCmd(commandEnvironment: CommandEnvironmentBean) : MusicCommandBase(commandEnvironment) {

	override val queueShouldNotBeEmpty = true

	/**
	 * Executes the logic for displaying the music queue.
	 *
	 * This method retrieves the current queue of tracks, calculates total and average durations, and builds paginated
	 * embed messages to show the queue. It also handles displaying the currently playing track with its remaining time.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeMusic(context: GuildCommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val paginatorChunkSize = environment.getProperty<Int>(BotProperty.JDA_PAGINATION_CHUNK_SIZE)

		val scheduler = manager.state.queueTrackScheduler
		val queue = scheduler.queue.iterable
		val lang = context.language
		val embedMessages = mutableListOf<MessageEmbed>()

		val elapsedTime = manager.cachedPlayer?.position ?: 0
		val currentTrack = manager.cachedPlayer?.track

		val totalDurationMillis = queue.sumOf { it.duration }
		val averageDurationMillis = queue.map(Track::duration).average().toLong()

		var trackIndex = 1
		for (chunk in queue.chunked(paginatorChunkSize)) {
			val messageBuilder = createEmbedMessage(context)
				.setTitle(I18nAudioSource.QUEUE)
				.setKeyValueField(I18nAudioSource.ALL_TRACKS_IN_QUEUE_COUNT, queue.size)
				.setSpace()
				.setKeyValueField(I18nAudioSource.ALL_TRACKS_IN_QUEUE_DURATION, millisToDTF(totalDurationMillis))
			currentTrack?.let {
				messageBuilder.setKeyValueField(
					I18nAudioSource.APPROX_TO_NEXT_TRACK_FROM_QUEUE,
					millisToDTF(currentTrack.duration - elapsedTime)
				)
			}
			messageBuilder
				.setSpace()
				.setKeyValueField(I18nAudioSource.PLAYLIST_AVERAGE_TRACK_DURATION, millisToDTF(averageDurationMillis))

			for (track in chunk) {
				val sender = context.guild.getMemberById(track.audioSender.authorId)

				val valueJoiner = StringJoiner("")
				valueJoiner.add(mdLink("[link]", track.uri))
				valueJoiner.add(", ${mdCode(millisToDTF(track.duration))}")
				sender?.let {
					valueJoiner.add(", ")
					valueJoiner.add(i18n.t(I18nAudioSource.TRACK_ADDED_BY, lang))
					valueJoiner.add(": ${mdBold(it.name)}")
				}
				messageBuilder.setKeyValueField(
					key = "${trackIndex++}. ${track.getTitle(normalized = true)}",
					value = valueJoiner.toString(),
					inline = false,
				)
			}
			val playlistRepeatDescription = if (scheduler.audioRepeat.playlistRepeat) {
				I18nUtilSource.TURN_ON
			} else {
				I18nUtilSource.TURN_OFF
			}
			messageBuilder
				.setColor(JdaColor.PRIMARY)
				.setFooter(I18nAudioSource.PLAYLIST_REPEATING_MODE, i18n.t(playlistRepeatDescription, lang))
			embedMessages.add(messageBuilder.build())
		}
		val paginator = createPaginator(context, embedMessages)
		val row = paginator.createPaginatorButtonsRow()
		val initMessage = paginator.initPaginator()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(initMessage)
			.addActionRows(row)
			.build()

		response.complete(commandResponse)
	}
}
