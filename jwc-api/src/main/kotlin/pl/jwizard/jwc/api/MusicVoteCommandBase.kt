package pl.jwizard.jwc.api

import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.i18n.source.I18nVotingSource
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.vote.I18nVoterResponse
import pl.jwizard.jwc.vote.VoterEnvironmentBean
import pl.jwizard.jwc.vote.music.MusicVoterComponent
import pl.jwizard.jwc.vote.music.MusicVoterResponse

abstract class MusicVoteCommandBase(
	private val voterEnvironment: VoterEnvironmentBean,
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment) {

	final override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val voterResponse = executeMusicVote(context, manager)
		val i18nResponse = I18nVoterResponse.Builder()
			.setInitMessage(initMessage, voterResponse.args)
			.setFailedMessage(failedMessage, voterResponse.args)
			.build()
		val musicVoter = MusicVoterComponent(
			context,
			i18nResponse,
			this::class,
			voterResponse.onSuccess,
			voterEnvironment,
			botEmojisCache
		)
		val (message, actionRow) = musicVoter.createInitVoterMessage()
		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addActionRows(actionRow)
			.onSendAction { musicVoter.initVoter(it) }
			.build()
		response.complete(commandResponse)
	}

	protected fun createVoteSuccessMessage(
		context: GuildCommandContext,
	) = createEmbedMessage(context).setTitle(I18nVotingSource.ON_SUCCESS_VOTING)

	// i81n key for vote content message
	protected abstract val initMessage: I18nResponseSource

	// i18n key for failed voting message
	protected abstract val failedMessage: I18nResponseSource

	protected abstract fun executeMusicVote(
		context: GuildCommandContext,
		manager: GuildMusicManager,
	): MusicVoterResponse
}
