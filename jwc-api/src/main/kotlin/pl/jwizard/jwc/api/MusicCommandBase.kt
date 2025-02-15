package pl.jwizard.jwc.api

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.ChannelType
import pl.jwizard.jwc.audio.client.AudioNodeType
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.PercentageIndicatorBar
import pl.jwizard.jwc.core.util.millisToDTF
import pl.jwizard.jwc.exception.audio.ActiveAudioPlayingNotFoundException
import pl.jwizard.jwc.exception.audio.PlayerNotPausedException
import pl.jwizard.jwc.exception.command.CommandAvailableOnlyForDiscreteTrackException
import pl.jwizard.jwc.exception.track.TrackQueueIsEmptyException
import pl.jwizard.jwl.i18n.I18nLocaleSource

abstract class MusicCommandBase(
	commandEnvironment: CommandEnvironmentBean,
) : AudioCommandBase(commandEnvironment) {
	final override fun executeAudio(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		// check, if user cannot try to use this command for continuous audio source (ex. radio)
		if (!manager.state.isDeclaredAudioContentTypeOrNotYetSet(AudioNodeType.QUEUED)) {
			throw CommandAvailableOnlyForDiscreteTrackException(context)
		}
		val player = manager.cachedPlayer
		val isActive = context.selfMember.voiceState?.channel?.type == ChannelType.VOICE
		// check if bot is on voice channel and play audio content
		val inPlayingMode = isActive && player?.track != null

		if (shouldPlayingMode && (!inPlayingMode || player?.paused == true) && !shouldPaused) {
			// throw when bot is not playing audio content (no tracks, currently paused etc.)
			throw ActiveAudioPlayingNotFoundException(context)
		}
		val queue = manager.state.queueTrackScheduler.queue

		// only for playing mode
		if (!shouldIdleMode) {
			if (shouldPaused && player?.paused == false) {
				// throw when user try to use command, when player is not paused
				throw PlayerNotPausedException(context)
			}
			val userVoiceState = checkUserVoiceState(context)
			if (!shouldEnabledOnFirstAction && (manager.cachedPlayer?.track != null || queue.isNotEmpty())
			) {
				// check, if user is together with bot on voice channel only for second-action commands
				// (command invoked, when audio content currently playing
				userIsWithBotOnAudioChannel(userVoiceState, context)
			}
		}
		if (queueShouldNotBeEmpty && queue.isEmpty()) {
			// throw when user try to use command when queue is empty
			throw TrackQueueIsEmptyException(context)
		}
		executeMusic(context, manager, response)
	}

	protected fun createDetailedTrackMessage(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		i18nTitle: I18nLocaleSource,
		i18nPosition: I18nLocaleSource,
		track: Track,
	): MessageEmbed {
		val elapsedTime = manager.cachedPlayer?.position ?: 0
		val audioSender = context.guild.getMemberById(track.audioSender.authorId)
		val percentageIndicatorBar = PercentageIndicatorBar(
			start = elapsedTime,
			total = track.duration,
		)
		val messageBuilder = createEmbedMessage(context)
			.setTitle(i18nTitle)
			.setKeyValueField(I18nAudioSource.TRACK_NAME, track.mdTitleLink)
		audioSender?.let {
			messageBuilder.setSpace()
			messageBuilder.setKeyValueField(I18nAudioSource.TRACK_ADDED_BY, it.user.name)
		}
		return messageBuilder.setValueField(percentageIndicatorBar.generateBar(), inline = false)
			.setKeyValueField(
				i18nPosition,
				"${millisToDTF(elapsedTime)} / ${millisToDTF(track.duration)}"
			)
			.setSpace()
			.setKeyValueField(
				I18nAudioSource.CURRENT_TRACK_LEFT_TO_NEXT,
				millisToDTF(track.duration - elapsedTime)
			)
			.setArtwork(track.thumbnailUrl)
			.setColor(JdaColor.PRIMARY)
			.build()
	}

	// available only when player is currently playing any audio content
	protected open val shouldPlayingMode = false

	// available only when player is currently not playing any audio content
	protected open val shouldIdleMode = false

	// available only when player is currently paused
	protected open val shouldPaused = false

	// available only when audio queue has any tracks
	protected open val queueShouldNotBeEmpty = false

	protected abstract fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse
	)
}
