package pl.jwizard.jwc.audio.loader.spinner

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.interaction.SelectSpinnerMenu
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.util.logger
import kotlin.math.min

class TrackSelectSpinnerMenu(
	private val guildMusicManager: GuildMusicManager,
	private val onEnqueueTrack: (track: Track) -> Unit,
	private val createTrackResponseMessage: (track: Track) -> MessageEmbed,
	options: List<TrackMenuOption>,
	guildMultipleProperties: GuildMultipleProperties,
) : SelectSpinnerMenu<TrackMenuOption>(guildMusicManager.state.context, options) {

	companion object {
		private val log = logger<TrackSelectSpinnerMenu>()
	}

	override val menuId = "track"

	override val elapsedTimeSec = guildMultipleProperties
		.getProperty<Long>(GuildProperty.TIME_AFTER_AUTO_CHOOSE_SEC)

	override val maxElementsToChoose = min(
		options.size, guildMultipleProperties.getProperty<Int>(GuildProperty.MAX_TRACKS_TO_CHOOSE)
	)

	override val randomChoice = guildMultipleProperties
		.getProperty<Boolean>(GuildProperty.RANDOM_AUTO_CHOOSE_TRACK)

	override fun onEvent(
		event: StringSelectInteractionEvent,
		context: CommandBaseContext,
		options: List<TrackMenuOption>,
	) {
		val option = options[0]
		onEnqueueTrack(option.track)
		guildMusicManager.sendMessage(createTrackResponseMessage((option.track)))
		log.jdaInfo(context, "Add track: %s after self-choose choice.", option.track.qualifier)
	}

	override fun onTimeout(context: CommandBaseContext, option: TrackMenuOption) {
		onEnqueueTrack(option.track)
		guildMusicManager.sendMessage(createTrackResponseMessage(option.track))
		log.jdaInfo(
			context,
			"Add track: %s after timeout: (%ds).",
			option.track.qualifier,
			elapsedTimeSec
		)
	}
}
