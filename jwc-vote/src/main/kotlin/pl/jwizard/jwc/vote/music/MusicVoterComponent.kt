package pl.jwizard.jwc.vote.music

import net.dv8tion.jda.api.entities.Member
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.emoji.BotEmojisCacheBean
import pl.jwizard.jwc.vote.I18nVoterResponse
import pl.jwizard.jwc.vote.VoterComponent
import pl.jwizard.jwc.vote.VoterEnvironmentBean
import kotlin.reflect.KClass

class MusicVoterComponent(
	private val context: GuildCommandContext,
	i18nResponse: I18nVoterResponse,
	clazz: KClass<*>,
	onSuccess: (response: TFutureResponse) -> Unit,
	voterEnvironment: VoterEnvironmentBean,
	botEmojisCache: BotEmojisCacheBean,
) : VoterComponent(context, i18nResponse, onSuccess, clazz, voterEnvironment, botEmojisCache) {

	override fun filterVotes(
		interactionAuthor: Member,
	) = voiceChannelWithBot?.members?.contains(interactionAuthor) ?: false

	override fun setTotalRatio() =
		voiceChannelWithBot?.members?.count { !it.user.isBot && context.selfMember != it } ?: 0

	private val voiceChannelWithBot
		get() = context.guild.voiceChannels.find { it.members.contains(context.selfMember) }
}
