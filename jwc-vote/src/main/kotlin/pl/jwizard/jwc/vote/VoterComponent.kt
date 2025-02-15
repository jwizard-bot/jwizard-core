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
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.jda.emoji.BotEmojisCacheBean
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.floatingSecToMin
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.i18n.I18nLocaleSource
import pl.jwizard.jwl.util.logger
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

abstract class VoterComponent(
	private val context: GuildCommandContext,
	private val response: I18nVoterResponse,
	private val onSuccess: (response: TFutureResponse) -> Unit,
	private val clazz: KClass<*>,
	voterEnvironment: VoterEnvironmentBean,
	botEmojisCache: BotEmojisCacheBean,
) : ButtonInteractionHandler(voterEnvironment.i18n, voterEnvironment.eventQueue, botEmojisCache) {

	companion object {
		private val log = logger<VoterComponent>()
	}

	private val i18n = voterEnvironment.i18n
	private val environment = voterEnvironment.environment
	private val jdaColorStore = voterEnvironment.jdaColorStore
	private val looselyTransportHandler = voterEnvironment.looselyTransportHandler

	private val maxVotingTime = environment.getGuildProperty<Long>(
		guildProperty = GuildProperty.MAX_VOTING_TIME_SEC,
		guildId = context.guild.idLong
	)

	private val percentageRatio = environment.getGuildProperty<Int>(
		guildProperty = GuildProperty.VOTING_PERCENTAGE_RATIO,
		guildId = context.guild.idLong,
	)

	private val voteState = VoteState(percentageRatio)

	private lateinit var message: Message

	fun initVoter(message: Message) {
		this.message = message
		initTimeoutEvent(maxVotingTime)
	}

	fun createInitVoterMessage(): Pair<MessageEmbed, ActionRow> {
		val initMessage = response.initMessage

		val message = MessageEmbedBuilder(i18n, jdaColorStore, context)
			.setTitle(I18nResponseSource.VOTE_POLL)
			.setDescription(initMessage.message, initMessage.args)
			.setFooter(I18nVotingSource.MAX_TIME_VOTING, floatingSecToMin(maxVotingTime))
			.build()
		return Pair(message, createButtons())
	}

	private fun checkVotes(event: ButtonInteractionEvent): Boolean {
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
			log.jdaInfo(
				context,
				"Member: %s was swap vote in: %s from: %s to: %s.",
				member.qualifier,
				clazz.simpleName,
				from,
				to
			)
		} else { // first voting for select member
			val result = when (id) {
				InteractionButton.YES.id -> voteState.addYesVote(member)
				InteractionButton.NO.id -> voteState.addNoVote(member)
				else -> return false
			}
			log.jdaInfo(
				context,
				"Member: %s was voted in: %s for: %s.",
				member.qualifier,
				clazz.simpleName,
				result
			)
		}
		val totalRatio = setTotalRatio()
		if (totalRatio == 0) {
			return false
		}
		return voteState.isPassedPositive(totalRatio) || voteState.isPassedNegative(totalRatio)
	}

	private fun createButtons(disabled: Boolean = false): ActionRow {
		val lang = context.language
		val (forYes, forNo) = voteState.votes
		val yesButton =
			createButton(InteractionButton.YES, lang, args = mapOf("forYes" to forYes), disabled)
		val noButton =
			createButton(InteractionButton.NO, lang, args = mapOf("forNo" to forNo), disabled)
		return ActionRow.of(yesButton, noButton)
	}

	private fun sendEndVoteMessage(
		i18nTitle: I18nLocaleSource,
		messageWithArgs: I18nMessageWithArgs<*>
	) {
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

	override val runForButtons = arrayOf(InteractionButton.YES, InteractionButton.NO)

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
				onSuccess(futureResponse)
				"successfully"
			} else {
				sendEndVoteMessage(I18nVotingSource.ON_FAILURE_VOTING, response.failedMessage)
				"negatively"
			}
			val (forYes, forNo) = voteState.votes
			log.jdaInfo(
				context,
				"Voting: %s was ended %s with YES/NO: %d/%d votes.",
				clazz.simpleName,
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

	override fun onTimeout() {
		val components = createButtons(disabled = true)
		message.editMessageComponents(components).queue {
			sendEndVoteMessage(I18nVotingSource.ON_TIMEOUT_VOTING, response.failedMessage)
			log.jdaInfo(context, "Voting: %s was ended by timeout.", clazz.simpleName)
			voteState.clear()
		}
	}

	// filters which members are allowed to vote, based on the interaction author
	protected abstract fun filterVotes(interactionAuthor: Member): Boolean

	// calculates the total vote ratio based on the current votes and guild settings
	protected abstract fun setTotalRatio(): Int
}
