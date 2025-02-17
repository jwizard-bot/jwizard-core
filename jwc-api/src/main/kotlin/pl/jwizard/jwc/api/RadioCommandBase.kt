package pl.jwizard.jwc.api

import pl.jwizard.jwc.audio.client.AudioNodeType
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.exception.radio.DiscreteAudioStreamIsPlayingException
import pl.jwizard.jwc.exception.radio.RadioStationIsNotPlayingException
import pl.jwizard.jwc.exception.radio.RadioStationIsPlayingException

internal abstract class RadioCommandBase(
	commandEnvironment: CommandEnvironment,
) : AudioCommandBase(commandEnvironment) {
	final override fun executeAudio(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse
	) {
		val voiceState = checkUserVoiceState(context)

		// check if user is with bot on same audio channel only when is not first-action command and
		// audio player is currently playing
		// first action commands are commands that can be run for the first time (ex. play) when the bot
		// does not is still on the voice channel with the user

		if (!shouldEnabledOnFirstAction && manager.cachedPlayer?.track != null) {
			userIsWithBotOnAudioChannel(voiceState, context)
		}
		val currentContent = manager.cachedPlayer?.track
		val isStreamContent = manager.state
			.isDeclaredAudioContentTypeOrNotYetSet(AudioNodeType.CONTINUOUS)

		// execute radio command only for continuous audio streams
		if (!isStreamContent && currentContent != null) {
			throw DiscreteAudioStreamIsPlayingException(context)
		}

		if (shouldRadioPlaying && (!isStreamContent || currentContent == null)) {
			// throw only if radio should play, but current playing content is null (not exist)
			throw RadioStationIsNotPlayingException(context)
		} else if (shouldRadioIdle && (isStreamContent && currentContent != null)) {
			// throw only if radio shouldn't play, but current playing any audio content
			throw RadioStationIsPlayingException(context)
		}
		executeRadio(context, manager, response)
	}

	// available only if radio is currently playing
	protected open val shouldRadioPlaying = false

	// available only if radio is currently not playing
	protected open val shouldRadioIdle = false

	protected abstract fun executeRadio(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	)
}
