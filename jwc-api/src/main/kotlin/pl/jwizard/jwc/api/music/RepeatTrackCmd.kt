package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.track.TrackRepeatsOutOfBoundException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.REPEAT_SET)
class RepeatTrackCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<RepeatTrackCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val shouldBeContentSenderOrSuperuser = true

	override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val repeatsCount = context.getArg<Int>(Argument.COUNT)

		// fetch guild-based properties from DB in single SQL query
		val multipleProperties = environment.getGuildMultipleProperties(
			guildProperties = listOf(
				GuildProperty.MIN_REPEATS_OF_TRACK,
				GuildProperty.MAX_REPEATS_OF_TRACK
			),
			guildId = context.guild.idLong
		)
		val minRepeats = multipleProperties.getProperty<Int>(GuildProperty.MIN_REPEATS_OF_TRACK)
		val maxRepeats = multipleProperties.getProperty<Int>(GuildProperty.MAX_REPEATS_OF_TRACK)

		// check, if repeats count incoming from user has exceeded declared min and max repeats in
		// guild settings
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
					"clearRepeatingCmd" to Command.REPEAT_CLEAR.parseWithPrefix(context),
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
