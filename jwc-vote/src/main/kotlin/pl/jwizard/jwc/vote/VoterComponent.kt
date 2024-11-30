/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.vote

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.interaction.ButtonInteractionHandler
import pl.jwizard.jwc.command.interaction.InteractionButton
import pl.jwizard.jwc.command.interaction.InteractionResponse
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.i18n.source.I18nVotingSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.jda.emoji.BotEmojisCacheBean
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.floatingSecToMin
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.i18n.I18nLocaleSource
import pl.jwizard.jwl.util.logger
import java.util.concurrent.CompletableFuture

/**
 * Abstract class for managing the voting process via buttons. Handles vote tracking, validating votes, and sending
 * results.
 *
 * @param T The type of the payload that will be processed after the vote.
 * @property context The command context related to the vote.
 * @property response Contains the response details and messages for the vote.
 * @property voterContent The content to process after a successful vote.
 * @property voterEnvironment The environment settings related to voting, including configurations or metadata.
 * @property botEmojisCache Cache containing the bot's custom emojis.
 * @author Miłosz Gilga
 */
abstract class VoterComponent<T : Any>(
	private val context: GuildCommandContext,
	private val response: I18nVoterResponse<T>,
	private val voterContent: VoterContent<T>,
	private val voterEnvironment: VoterEnvironmentBean,
	private val botEmojisCache: BotEmojisCacheBean,
) : ButtonInteractionHandler(voterEnvironment.i18n, voterEnvironment.eventQueue, botEmojisCache) {

	companion object {
		private val log = logger<VoterComponent<*>>()
	}

	private val i18n = voterEnvironment.i18n
	private val environment = voterEnvironment.environment
	private val jdaColorStore = voterEnvironment.jdaColorStore
	private val looselyTransportHandler = voterEnvironment.looselyTransportHandler

	/**
	 * The maximum time allowed for voting, retrieved from the guild's settings.
	 */
	private val maxVotingTime = environment.getGuildProperty<Long>(
		guildProperty = GuildProperty.MAX_VOTING_TIME_SEC,
		guildId = context.guild.idLong
	)

	/**
	 * The percentage ratio required for the vote to pass, retrieved from the guild's settings.
	 */
	private val percentageRatio = environment.getGuildProperty<Int>(
		guildProperty = GuildProperty.VOTING_PERCENTAGE_RATIO,
		guildId = context.guild.idLong,
	)

	/**
	 * The state of the current vote, including the tally of YES/NO votes and whether the vote passed.
	 */
	private val voteState = VoteState(percentageRatio)

	/**
	 * The message associated with the vote, which will be updated as votes are cast.
	 */
	private lateinit var message: Message

	/**
	 * Initializes the voter component with the given message and starts the timeout timer.
	 *
	 * @param message The message that initiates the vote.
	 */
	fun initVoter(message: Message) {
		this.message = message
		initTimeoutEvent(maxVotingTime)
	}

	/**
	 * Creates the initial vote message and action buttons (YES/NO) for the vote.
	 *
	 * @return A pair consisting of the initial vote message embed and the action row with buttons.
	 */
	fun createInitVoterMessage(): Pair<MessageEmbed, ActionRow> {
		val initMessage = response.initMessage

		val message = MessageEmbedBuilder(i18n, jdaColorStore, context)
			.setTitle(I18nResponseSource.VOTE_POLL)
			.setDescription(initMessage.message, initMessage.args)
			.setFooter(I18nVotingSource.MAX_TIME_VOTING, floatingSecToMin(maxVotingTime))
			.build()
		return Pair(message, createButtons())
	}

	/**
	 * Validates and handles the votes cast via button interactions.
	 *
	 * @param event The button interaction event triggered by the user's vote.
	 * @return `true` if the voting has ended (positive or negative result), `false` otherwise.
	 */
	private fun checkVotes(event: ButtonInteractionEvent): Boolean {
		val className = voterContent.javaClass.simpleName
		val member = event.member ?: return false
		if (!filterVotes(member)) {
			return false
		}
		val id = getComponentId(event.componentId)
		if (voteState.isMemberReVoting(member)) { // re-voting from same user
			val (from, to) = when (id) {
				InteractionButton.NO.id -> voteState.swapYesToNoVote()
				InteractionButton.YES.id -> voteState.swapNoToYesVote()
				else -> return false
			}
			log.jdaInfo(context, "Member: %s was swap vote in: %s from: %s to: %s.", member.qualifier, className, from, to)
		} else { // first voting for select member
			val result = when (id) {
				InteractionButton.YES.id -> voteState.addYesVote(member)
				InteractionButton.NO.id -> voteState.addNoVote(member)
				else -> return false
			}
			log.jdaInfo(context, "Member: %s was voted in: %s for: %s.", member.qualifier, className, result)
		}
		val totalRatio = setTotalRatio()
		if (totalRatio == 0) {
			return false
		}
		return voteState.isPassedPositive(totalRatio) || voteState.isPassedNegative(totalRatio)
	}

	/**
	 * Creates action buttons for voting (YES/NO), with an option to disable them.
	 *
	 * @param disabled Whether the buttons should be disabled (e.g., when voting ends).
	 * @return An action row containing the YES and NO buttons.
	 */
	private fun createButtons(disabled: Boolean = false): ActionRow {
		val lang = context.language
		val (forYes, forNo) = voteState.votes
		val yesButton = createButton(InteractionButton.YES, lang, args = mapOf("forYes" to forYes), disabled)
		val noButton = createButton(InteractionButton.NO, lang, args = mapOf("forNo" to forNo), disabled)
		return ActionRow.of(yesButton, noButton)
	}

	/**
	 * Sends a message to indicate that the vote has ended, either due to success or failure.
	 *
	 * @param i18nTitle The localized title for the end-of-vote message.
	 * @param messageWithArgs The localized message with arguments.
	 */
	private fun sendEndVoteMessage(i18nTitle: I18nLocaleSource, messageWithArgs: I18nMessageWithArgs<*>) {
		val message = MessageEmbedBuilder(i18n, jdaColorStore, context)
			.setTitle(i18nTitle)
			.setDescription(messageWithArgs.message, messageWithArgs.args)
			.setColor(JdaColor.PRIMARY)
			.build()
		looselyTransportHandler.sendViaChannelTransport(
			textChannel = context.textChannel,
			response = CommandResponse.Builder().addEmbedMessages(message).build(),
			notificationsSuppressed = context.suppressResponseNotifications,
		)
	}

	/**
	 * Listens for button interactions (YES/NO) during the voting period.
	 */
	override val runForButtons = arrayOf(InteractionButton.YES, InteractionButton.NO)

	/**
	 * Executes the button interaction event, handling votes and checking if the voting has ended.
	 *
	 * @param event The button interaction event.
	 * @return The interaction response, containing updated components or status.
	 */
	override fun executeEvent(event: ButtonInteractionEvent): InteractionResponse {
		val votingEnded = checkVotes(event)
		if (votingEnded) {
			val endedResult = if (voteState.isPassed) {
				val futureResponse = CompletableFuture<CommandResponse>()
				futureResponse.thenAccept {
					looselyTransportHandler.sendViaChannelTransport(
						context.textChannel,
						it,
						context.suppressResponseNotifications,
					)
				}
				voterContent.afterSuccess(context, futureResponse, response.payload)
				"successfully"
			} else {
				sendEndVoteMessage(I18nVotingSource.ON_FAILURE_VOTING, response.failedMessage)
				"negatively"
			}
			val (forYes, forNo) = voteState.votes
			log.jdaInfo(
				context,
				"Voting: %s was ended %s with YES/NO: %d/%d votes.",
				voterContent.javaClass.simpleName,
				endedResult,
				forYes,
				forNo
			)
		}
		val (message, actionRow) = createInitVoterMessage()
		val modifiedButtons = if (votingEnded) {
			ActionRow.of(actionRow.buttons.map(Button::asDisabled))
		} else {
			actionRow
		}
		voteState.clear()
		return InteractionResponse(
			interactionCallback = { it.editOriginalEmbeds(message).setComponents(modifiedButtons) },
			refreshableEvent = !votingEnded,
		)
	}

	/**
	 * Handles the timeout event for the vote, disabling buttons and sending a timeout message.
	 */
	override fun onTimeout() {
		val components = createButtons(disabled = true)
		message.editMessageComponents(components).queue {
			sendEndVoteMessage(I18nVotingSource.ON_TIMEOUT_VOTING, response.failedMessage)
			log.jdaInfo(context, "Voting: %s was ended by timeout.", voterContent.javaClass.simpleName)
			voteState.clear()
		}
	}

	/**
	 * Filters which members are allowed to vote, based on the interaction author.
	 *
	 * @param interactionAuthor The member who interacted with the vote button.
	 * @return `true` if the member is allowed to vote, `false` otherwise.
	 */
	protected abstract fun filterVotes(interactionAuthor: Member): Boolean

	/**
	 * Calculates the total vote ratio based on the current votes and guild settings.
	 *
	 * @return The total ratio of YES/NO votes.
	 */
	protected abstract fun setTotalRatio(): Int
}
