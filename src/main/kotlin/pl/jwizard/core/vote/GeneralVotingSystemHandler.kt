/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.vote

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.UnicodeEmoji
import pl.jwizard.core.exception.UserException
import pl.jwizard.core.log.AbstractLoggingBean
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.requests.RestAction

class GeneralVotingSystemHandler(
	private val response: VoteResponseData,
	private val event: CompoundCommandEvent,
	private val botConfiguration: BotConfiguration,
) : VotingSystemHandler, AbstractLoggingBean(GeneralVotingSystemHandler::class) {

	private val forYes = AtomicInteger()
	private val forNo = AtomicInteger()
	private val requiredVotes = AtomicInteger()

	private val succeed = AtomicBoolean()
	private val voters = mutableListOf<User>()

	private val emojisCallback: (message: Message) -> RestAction<List<Void>> = {
		RestAction.allOf(
			it.addReaction(UnicodeEmoji.THUMBS_UP.code),
			it.addReaction(UnicodeEmoji.THUMBS_DOWN.code),
		)
	}

	override fun initAndStart() {
		jdaLog.info(event, "Initialized voting from: ${response.initClazz.simpleName}")
		val hook = event.slashCommandEvent?.hook
		val messageAction = if (hook?.isExpired == false) {
			hook.sendMessageEmbeds(response.message)
		} else {
			event.textChannel.sendMessageEmbeds(response.message)
		}
		messageAction.queue { message -> emojisCallback(message).queue { fabricateEventWaiter(message) } }
	}

	private fun fabricateEventWaiter(message: Message) {
		val guildDetails = botConfiguration.guildSettings.getGuildProperties(event.guildId)
		val predictorData = VotePredictorData(forYes, forNo, requiredVotes, succeed, voters, response, message)
		botConfiguration.eventWaiter.waitForEvent(
			GuildMessageReactionAddEvent::class.java,
			{ onAfterVotePredicate(it, predictorData) },
			{ onAfterFinishVoting(predictorData) },
			guildDetails.voting.timeToFinishSec,
			TimeUnit.SECONDS,
			{ onAfterTimeout(predictorData) },
		)
	}

	private fun onAfterVotePredicate(gEvent: GuildMessageReactionAddEvent, predictorData: VotePredictorData): Boolean {
		if (gEvent.messageId != predictorData.message.id || gEvent.user.isBot) {
			return false // end on different message or bot self-instance
		}
		val emote = gEvent.reactionEmote
		if (!emote.isEmoji) {
			return false // end on non emoji
		}
		val voiceChannelWithBot = gEvent.guild.voiceChannels
			.find { it.members.contains(gEvent.guild.selfMember) }
			?: throw UserException.UserOnVoiceChannelWithBotNotFoundException(event)

		// if user send emoji, when is not on voice channel, remove emojis from him
		if (!voiceChannelWithBot.members.contains(gEvent.member)) {
			removeEmoji(gEvent, predictorData, UnicodeEmoji.THUMBS_UP)
			removeEmoji(gEvent, predictorData, UnicodeEmoji.THUMBS_DOWN)
			return false
		}
		if (predictorData.votedUsers.contains(gEvent.user)) { // revoting from same user
			if (UnicodeEmoji.THUMBS_DOWN.checkEquals(emote)) {
				removeEmoji(gEvent, predictorData, UnicodeEmoji.THUMBS_UP)
				predictorData.forYes.decrementAndGet()
				predictorData.forNo.incrementAndGet()
				jdaLog.info(event, "Member: ${gEvent.user.asTag} was re-voted for NO from YES")
			} else if (UnicodeEmoji.THUMBS_UP.checkEquals(emote)) {
				removeEmoji(gEvent, predictorData, UnicodeEmoji.THUMBS_DOWN)
				predictorData.forYes.incrementAndGet()
				predictorData.forNo.decrementAndGet()
				jdaLog.info(event, "Member: ${gEvent.user.asTag} was re-voted for YES from NO")
			} else {
				return false // unsupported emoji
			}
		} else { // on first voting for user
			if (UnicodeEmoji.THUMBS_DOWN.checkEquals(emote)) {
				predictorData.forNo.incrementAndGet()
				jdaLog.info(event, "Member: ${gEvent.user.asTag} was voted for NO")
			} else if (UnicodeEmoji.THUMBS_UP.checkEquals(emote)) {
				predictorData.forYes.incrementAndGet()
				jdaLog.info(event, "Member: ${gEvent.user.asTag} was voted for YES")
			} else {
				return false // unsupported emoji
			}
			predictorData.votedUsers.add(gEvent.user)
		}
		val botMember = gEvent.guild.selfMember
		val totalMembersOnChannel = voiceChannelWithBot.members.count { it.user.isBot && botMember != it }
		if (totalMembersOnChannel == 0) {
			return false // no members found on channel, end voting
		}
		val ratioFabricator: (votes: AtomicInteger) -> Double =
			{ (1.0 * it.get().toDouble() / totalMembersOnChannel.toDouble()) * 100 }

		val guildDetails = botConfiguration.guildSettings.getGuildProperties(gEvent.guild.id)

		val differentYesRatio = ratioFabricator(predictorData.forYes)
		val differentNoRatio = ratioFabricator(predictorData.forNo)
		val percentageRatio = guildDetails.voting.percentageRatio

		predictorData.required.set(ceil(totalMembersOnChannel * (percentageRatio.toDouble() / 100)).toInt())
		if (differentYesRatio >= percentageRatio) {
			predictorData.succeed.set(true)
			return true // end voting with passed result
		}
		if (differentNoRatio >= percentageRatio) {
			predictorData.succeed.set(false)
			return true // end voting with not-acceptable result
		}
		return false
	}

	private fun onAfterFinishVoting(predictorData: VotePredictorData) {
		clearData(predictorData)
		val endingCallback = if (predictorData.succeed.get()) {
			jdaLog.info(event, "Voting execution was ended with successfully result")
			predictorData.response.onSuccess
		} else {
			jdaLog.info(event, "Voting execution was ended with failure result")
			predictorData.response.onFailure
		}
		val voteFinishData = VoteFinishData(predictorData)
		event.instantlySendEmbedMessage(endingCallback(voteFinishData), legacyTransport = true)
		clearVotingState(predictorData)
	}

	private fun onAfterTimeout(predictorData: VotePredictorData) {
		clearData(predictorData)

		val voteFinishData = VoteFinishData(predictorData)
		jdaLog.info(event, "Voting execution was timeouted")

		event.instantlySendEmbedMessage(predictorData.response.onTimeout(voteFinishData), legacyTransport = true)
		clearVotingState(predictorData)
	}

	private fun clearData(votePredictorData: VotePredictorData) {
		votePredictorData.message.clearReactions().queue()
		votePredictorData.votedUsers.clear()
	}

	private fun clearVotingState(votePredictorData: VotePredictorData) {
		votePredictorData.forYes.set(0)
		votePredictorData.forNo.set(0)
		votePredictorData.succeed.set(false)
		votePredictorData.required.set(0)
	}

	private fun removeEmoji(
		gEvent: GuildMessageReactionAddEvent,
		predictorData: VotePredictorData,
		emoji: UnicodeEmoji
	) = predictorData.message.removeReaction(emoji.code, gEvent.user).queue()
}
