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
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.ext.thumbnailUrl
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.track.TrackRepeatsOutOfBoundException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.util.logger

/**
 * Command to set the number of times the currently playing track should repeat.
 *
 * This command allows the user to specify how many times the currently playing track will repeat. It checks that the
 * requested repeat count is within valid bounds and updates the music manager accordingly.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.REPEAT)
class RepeatTrackCmd(commandEnvironment: CommandEnvironmentBean) : MusicCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<RepeatTrackCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val shouldBeContentSenderOrSuperuser = true

	/**
	 * Executes the repeat command to set the number of times the current track will repeat.
	 *
	 * This method retrieves the repeat count from the command context, checks if it is within the valid bounds, and
	 * updates the track scheduler with the specified repeat count. If the count is out of bounds, it throws a
	 * [TrackRepeatsOutOfBoundException].
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeMusic(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val repeatsCount = context.getArg<Int>(Argument.COUNT)

		val multipleProperties = environmentBean.getGuildMultipleProperties(
			guildProperties = listOf(GuildProperty.MIN_REPEATS_OF_TRACK, GuildProperty.MAX_REPEATS_OF_TRACK),
			guildId = context.guild.idLong
		)
		val minRepeats = multipleProperties.getProperty<Int>(GuildProperty.MIN_REPEATS_OF_TRACK)
		val maxRepeats = multipleProperties.getProperty<Int>(GuildProperty.MAX_REPEATS_OF_TRACK)

		if (repeatsCount < minRepeats || repeatsCount > maxRepeats) {
			throw TrackRepeatsOutOfBoundException(context, minRepeats, maxRepeats)
		}
		manager.state.queueTrackScheduler.updateCountOfRepeats(repeatsCount)

		val currentPlayingTrack = manager.cachedPlayer?.track
		log.jdaInfo(
			context,
			"Repeating of current playing track: %s will be repeating: %d times.",
			currentPlayingTrack?.qualifier,
			repeatsCount
		)
		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.SET_MULTIPLE_REPEATING_TRACK,
				args = mapOf(
					"track" to currentPlayingTrack?.mdTitleLink,
					"times" to repeatsCount,
					"clearRepeatingCmd" to Command.REPEATCLS.parseWithPrefix(context.prefix),
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
