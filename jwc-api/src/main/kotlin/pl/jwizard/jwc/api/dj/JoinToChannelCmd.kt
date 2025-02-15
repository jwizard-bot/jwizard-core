package pl.jwizard.jwc.api.dj

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.exceptions.PermissionException
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.mdCode
import pl.jwizard.jwc.exception.command.InsufficientPermissionsException
import pl.jwizard.jwc.exception.user.UserIsAlreadyWithBotException
import pl.jwizard.jwc.exception.user.UserOnVoiceChannelNotFoundException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.JOIN)
class JoinToChannelCmd(
	commandEnvironment: CommandEnvironmentBean
) : DjCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<JoinToChannelCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldAllowAlsoForAllContentSender = true

	override fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		// get voice channel from guild with member which invoked command, if member not in channel
		// throw exception
		val voiceChannelWithMember = context.guild.voiceChannels
			.find { mapMembersToIds(it).contains(context.author.idLong) }
			?: throw UserOnVoiceChannelNotFoundException(context)

		// check, if user cannot try to move bot to same voice channel
		if (mapMembersToIds(voiceChannelWithMember).contains(context.selfMember.idLong)) {
			throw UserIsAlreadyWithBotException(context, voiceChannelWithMember)
		}
		context.selfMember.let {
			try {
				context.guild.moveVoiceMember(it, voiceChannelWithMember).complete()
				log.jdaInfo(
					context,
					"Bot was successfully moved to channel: %s",
					voiceChannelWithMember.qualifier
				)
			} catch (ex: PermissionException) {
				// throw only when member whose try to move bot to another channel has not moving members
				// permissions
				throw InsufficientPermissionsException(context, mdCode(ex.permission.name), ex.permission)
			}
		}
		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.MOVE_BOT_TO_SELECTED_CHANNEL,
				args = mapOf("movedChannel" to voiceChannelWithMember.name),
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}

	// map channel members to his ids
	private fun mapMembersToIds(channel: VoiceChannel) = channel.members.map(Member::getIdLong)
}
