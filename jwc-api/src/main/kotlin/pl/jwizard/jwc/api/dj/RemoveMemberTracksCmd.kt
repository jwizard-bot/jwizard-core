package pl.jwizard.jwc.api.dj

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.util.ext.name
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.mdCode
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwc.core.util.millisToDTF
import pl.jwizard.jwc.exception.user.UserNotAddedTracksToQueueException
import pl.jwizard.jwc.exception.user.UserNotFoundInGuildException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.QUEUE_REMOVE)
class RemoveMemberTracksCmd(
	commandEnvironment: CommandEnvironmentBean
) : DjCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<RemoveMemberTracksCmd>()
	}

	override val shouldOnSameChannelWithBot = true

	override fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val userId = context.getArg<Long>(Argument.MEMBER)
		// max elements per one page
		val paginatorChunkSize = environment.getProperty<Int>(BotProperty.JDA_PAGINATION_CHUNK_SIZE)

		val member = context.guild.members.find { it.idLong == userId }
			?: throw UserNotFoundInGuildException(context, userId)

		val queue = manager.state.queueTrackScheduler.queue

		// check, if selected member added any track to current audio queue, if not throw exception
		val userAddAnyTrackToQueue = queue.iterable.any {
			manager.cachedPlayer?.track?.audioSender?.authorId == member.idLong
		}
		if (!userAddAnyTrackToQueue) {
			throw UserNotAddedTracksToQueueException(context, userId)
		}
		val removedTracks = queue.removePositionsFromUser(userId)
		log.jdaInfo(
			context,
			"Remove: %d tracks added by user: %s.",
			removedTracks.size,
			member.qualifier
		)

		val embedMessages = mutableListOf<MessageEmbed>()
		for ((index, chunk) in removedTracks.chunked(paginatorChunkSize).withIndex()) {
			val messageBuilder = createEmbedMessage(context)
				.setTitle(I18nAudioSource.REMOVED_POSITIONS)
				.setDescription(
					i18nLocaleSource = I18nResponseSource.REMOVED_TRACKS_FROM_SELECTED_MEMBER,
					args = mapOf(
						"countOfRemovedTracks" to removedTracks.size,
						"memberTag" to member.name,
					),
				)
			for (track in chunk) {
				messageBuilder.setKeyValueField(
					key = "${index + 1}. ${track.getTitle(normalized = true)}",
					value = "${mdLink("[link]", track.uri)}, ${mdCode(millisToDTF(track.duration))}",
					inline = false,
				)
			}
			messageBuilder.setColor(JdaColor.PRIMARY)
			embedMessages.add(messageBuilder.build())
		}
		val paginator = createPaginator(context, embedMessages)
		val initMessage = paginator.initPaginator()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(initMessage)
			.addActionRows(paginator.paginatorButtonsRow)
			.build()

		response.complete(commandResponse)
	}
}
