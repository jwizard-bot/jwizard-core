/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.vote

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.requests.RestAction
import org.apache.commons.lang3.StringUtils
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.embed.UnicodeEmoji
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.Formatter
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SongChooserVotingSystemHandler(
	private val loadedTracks: List<AudioTrack>,
	val botConfiguration: BotConfiguration,
	val event: CompoundCommandEvent,
	val onSelectTrackCallback: (audioTrack: AudioTrack) -> Unit,
	private val lockedGuilds: MutableList<String>,
) : VotingSystemHandler, AbstractLoggingBean(SongChooserVotingSystemHandler::class) {

	private val selectedIndex = AtomicInteger()

	private var elapsedTimeInSec = 0L
	private var countOfMaxTracks = 0
	private var isRandom = true
	private var trimmedTracks = listOf<AudioTrack>()

	override fun initAndStart() {
		lockedGuilds.add(event.guildId)

		val guildDetails = botConfiguration.guildSettingsSupplier.fetchVotingSongChooserSettings(event.guildDbId)
		isRandom = guildDetails.randomAutoChooseTrack
		elapsedTimeInSec = guildDetails.timeAfterAutoChooseSec.toLong()
		countOfMaxTracks = guildDetails.tracksToChooseMax

		trimmedTracks = loadedTracks.subList(0, countOfMaxTracks)
		jdaLog.info(
			event,
			"Initialized voting for select song from results list: ${trimmedTracks.joinToString { it.info.title }}"
		)
		val joiner = StringJoiner(StringUtils.EMPTY)
		val initEmbedMessage = botConfiguration.i18nService.getMessage(
			i18nLocale = I18nResLocale.SELECT_SONG_SEQUENCER,
			params = mapOf(
				"resultsFound" to trimmedTracks.size,
				"elapsedTime" to elapsedTimeInSec,
				"afterTimeResult" to botConfiguration.i18nService.getMessage(
					if (isRandom) I18nMiscLocale.RANDOM_RESULT else I18nMiscLocale.FIRST_RESULT,
					event.lang
				),
			),
			event.lang,
		)
		joiner.add(initEmbedMessage)
		joiner.add("\n\n")
		for (index in trimmedTracks.indices) {
			joiner.add("`${index}` ")
			joiner.add(Formatter.createRichTrackTitle(trimmedTracks[index]))
			joiner.add("\n")
		}
		val embedMessage = CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.setDescription(joiner.toString())
			.setColor(EmbedColor.WHITE.color())
			.build()

		val hook = event.slashCommandEvent?.hook
		val messageAction = if (hook?.isExpired == false) {
			hook.sendMessageEmbeds(embedMessage)
		} else {
			event.textChannel.sendMessageEmbeds(embedMessage)
		}
		val emojisCallback: (message: Message) -> RestAction<List<Void>> = { message ->
			RestAction.allOf(
				UnicodeEmoji.getNumbers(countOfMaxTracks).map { message.addReaction(it.createEmoji()) })
		}
		messageAction.queue { message -> emojisCallback(message).queue { fabricateEventWaiter(message) } }
	}

	private fun fabricateEventWaiter(message: Message) {
		botConfiguration.eventWaiter.waitForEvent(
			MessageReactionAddEvent::class.java,
			{ onAfterSelect(it, message) },
			{
				lockedGuilds.remove(event.guildId)
				onSelectTrackCallback(trimmedTracks[selectedIndex.get()])
			},
			elapsedTimeInSec,
			TimeUnit.SECONDS,
			{ onAfterTimeout(message) }
		)
	}

	private fun onAfterSelect(gEvent: MessageReactionAddEvent, message: Message): Boolean {
		if (gEvent.messageId != message.id || gEvent.user?.isBot == true) {
			return false // end on different message or bot self-instance
		}
		val emoji = gEvent.reaction.emoji
		if (gEvent.user != null && gEvent.userId != event.author.id) {
			message.removeReaction(emoji, gEvent.user as User).queue()
			return false // skip for non-invoking voting user
		}
		val selectedEmoji = UnicodeEmoji.getNumbers(countOfMaxTracks)
			.find { it.code == emoji.asReactionCode }
			?: return false

		val index = selectedEmoji.index
		selectedIndex.set(index)
		message.clearReactions().queue()
		jdaLog.info(
			event,
			"Selecting track was ended successfully. Selected track ($selectedIndex): ${
				Formatter.trackStr(trimmedTracks[index])
			}"
		)
		return true
	}

	private fun onAfterTimeout(message: Message) {
		val selectedIndex = if (isRandom) {
			Random().nextInt(0, trimmedTracks.size)
		} else {
			0
		}
		val selectedTrack = trimmedTracks[selectedIndex]
		jdaLog.info(
			event,
			"Selecting track from results is ended. Selected track (${selectedIndex}): ${
				Formatter.trackStr(selectedTrack)
			}"
		)
		message.clearReactions().queue()
		lockedGuilds.remove(event.guildId)
		onSelectTrackCallback(selectedTrack)
	}
}
