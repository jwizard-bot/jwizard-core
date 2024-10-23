/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.vote.music

import net.dv8tion.jda.api.entities.Member
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.vote.I18nVoterResponse
import pl.jwizard.jwc.vote.VoterComponent
import pl.jwizard.jwc.vote.VoterContent

/**
 * A component that manages music voting within a voice channel.
 *
 * This class extends [VoterComponent] and provides functionality specific to music voting, including filtering votes
 * based on voice channel membership and calculating the total number of eligible voters.
 *
 * @param T The type of the payload associated with the voting process.
 * @property context The context in which the command is executed, providing access to command-related data.
 * @property i18nResponse The internationalized response associated with the voting process.
 * @property voterContent The content that will be processed after a successful vote.
 * @property commandEnvironment The environment in which the command is executed, providing access to various services.
 * @author Miłosz Gilga
 */
class MusicVoterComponent<T : Any>(
	private val context: CommandContext,
	private val i18nResponse: I18nVoterResponse<T>,
	private val voterContent: VoterContent<T>,
	private val commandEnvironment: CommandEnvironmentBean,
) : VoterComponent<T>(context, i18nResponse, voterContent, commandEnvironment) {

	/**
	 * Filters votes based on the interaction author's membership in the voice channel with the bot.
	 *
	 * @param interactionAuthor The member who is attempting to vote.
	 * @return `true` if the member is in the same voice channel as the bot; `false` otherwise.
	 */
	override fun filterVotes(interactionAuthor: Member): Boolean {
		val voiceChannelWithBot = getVoiceChannelWithBot()
		return voiceChannelWithBot?.members?.contains(interactionAuthor) ?: false
	}

	/**
	 * Sets the total ratio of eligible voters based on the number of members in the voice channel.
	 *
	 * @return The count of non-bot members in the voice channel excluding the bot itself.
	 */
	override fun setTotalRatio(): Int {
		val voiceChannelWithBot = getVoiceChannelWithBot()
		return voiceChannelWithBot?.members?.count { !it.user.isBot && context.selfMember != it } ?: 0
	}

	/**
	 * Retrieves the voice channel that contains the bot.
	 *
	 * @return The voice channel with the bot, or `null` if no such channel exists.
	 */
	private fun getVoiceChannelWithBot() = context.guild.voiceChannels.find { it.members.contains(context.selfMember) }
}
