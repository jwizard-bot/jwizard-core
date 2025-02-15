package pl.jwizard.jwc.api

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildVoiceState
import net.dv8tion.jda.api.entities.channel.ChannelType
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.AsyncUpdatableHandler
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.exception.audio.TemporaryHaltedBotException
import pl.jwizard.jwc.exception.command.ForbiddenChannelException
import pl.jwizard.jwc.exception.command.InvokerIsNotSenderOrSuperuserException
import pl.jwizard.jwc.exception.user.UserOnVoiceChannelNotFoundException
import pl.jwizard.jwc.exception.user.UserOnVoiceChannelWithBotNotFoundException

abstract class AudioCommandBase(
	commandEnvironment: CommandEnvironmentBean,
) : CommandBase(commandEnvironment) {
	final override fun execute(context: GuildCommandContext, response: TFutureResponse) {
		val musicManager = commandEnvironment.musicManagers
			.getOrCreateMusicManager(context, response, commandEnvironment.audioClient)

		val musicTextChannel = context.musicTextChannelId?.let { context.guild.getTextChannelById(it) }
		val musicTextChannelId = musicTextChannel?.idLong

		// check invoking channel id
		if (musicTextChannelId != null && context.textChannel.idLong != musicTextChannelId) {
			throw ForbiddenChannelException(context, context.textChannel, musicTextChannel)
		}
		// check, if bot (self member) is not currently muted
		if (context.selfMember.voiceState?.isMuted == true) {
			throw TemporaryHaltedBotException(context)
		}
		// check, if content sender is sender or superuser
		if (shouldBeContentSenderOrSuperuser) {
			val (isSender, isDj, isSuperUser) = checkPermissions(context, musicManager)
			if (!isSender && !isDj && !isSuperUser) {
				throw InvokerIsNotSenderOrSuperuserException(context)
			}
		}
		executeAudio(context, musicManager, response)
	}

	protected fun checkPermissions(
		context: GuildCommandContext,
		manager: GuildMusicManager,
	): Triple<Boolean, Boolean, Boolean> {
		val isSender = manager.cachedPlayer?.track?.audioSender?.authorId == context.author.idLong
		val isSuperUser = context.checkIfAuthorHasPermissions(*(superuserPermissions.toTypedArray()))
		val isDj = context.checkIfAuthorHasRoles(context.djRoleName)
		return Triple(isSender, isDj, isSuperUser)
	}

	protected fun checkUserVoiceState(context: GuildCommandContext): GuildVoiceState {
		val userVoiceState = context.author.voiceState
		if (userVoiceState?.channel?.type != ChannelType.VOICE) {
			throw UserOnVoiceChannelNotFoundException(context)
		}
		val afkChannel = context.guild.afkChannel
		if (userVoiceState.channel == afkChannel) {
			// check, if user is on afk channel, sending commands from afk channel is forbidden
			throw ForbiddenChannelException(context, afkChannel, context.textChannel)
		}
		return userVoiceState
	}

	protected fun userIsWithBotOnAudioChannel(
		voiceState: GuildVoiceState,
		context: GuildCommandContext,
	) {
		val botVoiceState = context.selfMember.voiceState
		val superuserPermissions = environment
			.getListProperty<String>(BotListProperty.JDA_SUPERUSER_PERMISSIONS)

		val isRegularUser = superuserPermissions.none {
			context.author.hasPermission(Permission.valueOf(it))
		}
		val differentAudioChannels = botVoiceState?.channel?.id != voiceState.channel?.id

		// check, if regular user is on the same channel with bot (omit for admin and server moderator)
		if (shouldOnSameChannelWithBot && differentAudioChannels && isRegularUser) {
			throw UserOnVoiceChannelWithBotNotFoundException(
				context,
				voiceState.channel,
				botVoiceState?.channel
			)
		}
	}

	protected fun createAsyncUpdatablePlayerHandler(
		context: GuildCommandContext,
		response: TFutureResponse,
	) = AsyncUpdatableHandler(context, response, this::class, exceptionTrackerHandler)

	// disable checking, if bot is on same audio channel with user for first-action command (ex. if
	// user invoke play command to join bot to channel)
	protected open val shouldEnabledOnFirstAction = false

	// available only if user is on same audio channel with bot
	protected open val shouldOnSameChannelWithBot = false

	// available only for content sender or admin/moderator/dj
	protected open val shouldBeContentSenderOrSuperuser = false

	protected abstract fun executeAudio(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	)
}
