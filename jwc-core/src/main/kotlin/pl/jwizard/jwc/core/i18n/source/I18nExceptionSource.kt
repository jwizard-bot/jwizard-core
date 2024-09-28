/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource
import pl.jwizard.jwc.core.integrity.ReferentialIntegrityChecker

/**
 * Provides internationalization (i18n) placeholders for various exception-related messages.
 *
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 * @see I18nBean
 */
enum class I18nExceptionSource(override val placeholder: String) : I18nLocaleSource, ReferentialIntegrityChecker {
	UNEXPECTED_EXCEPTION("jwc.exception.unexpectedException"),
	MODULE_IS_TURNED_OFF("jwc.exception.moduleIsTurnedOffException"),
	EPHEMERAL_UNEXPECTED_EXCEPTION("jwc.exception.ephemeralUnexpectedException"),
	TRACK_OFFSET_OUT_OF_BOUNDS("jwc.exception.trackOffsetOutOfBoundsException"),
	TRACK_THE_SAME_POSITION("jwc.exception.trackTheSamePositionException"),
	TRACK_QUEUE_IS_EMPTY("jwc.exception.trackQueueIsEmptyException"),
	TRACK_IS_NOT_PLAYING("jwc.exception.trackIsNotPlayingException"),
	INVOKER_IS_NOT_TRACK_SENDER_OR_ADMIN("jwc.exception.invokerIsNotTrackSenderOrAdminException"),
	USER_NOT_ADDED_TRACKS_TO_QUEUE("jwc.exception.userNotAddedTracksToQueueException"),
	TRACK_IS_NOT_PAUSED("jwc.exception.trackIsNotPausedException"),
	ACTIVE_MUSIC_PLAYING_NOT_FOUND("jwc.exception.activeMusicPlayingNotFoundException"),
	TEMPORARY_HALTED_BOT("jwc.exception.temporaryHaltedBotException"),
	TRACK_REPEATS_OUT_OF_BOUNDS("jwc.exception.trackRepeatsOutOfBoundsException"),
	ISSUE_WHILE_PLAYING_TRACK("jwc.exception.unexpectedErrorOnPlayTrack"),
	ISSUE_WHILE_LOADING_TRACK("jwc.exception.unexpectedErrorOnLoadTrack"),
	NOT_FOUND_TRACK("jwc.exception.notFoundAudioTrack"),
	USER_ID_ALREADY_WITH_BOT("jwc.exception.userIsAlreadyWithBotException"),
	USER_NOT_FOUND_IN_GUILD("jwc.exception.userNotFoundInGuildException"),
	UNAUTHORIZED_DJ("jwc.exception.unauthorizedDjException"),
	UNAUTHORIZED_DJ_OR_SENDER("jwc.exception.unauthorizedDjOrSenderException"),
	UNAUTHORIZED_MANAGER("jwc.exception.unauthorizedManagerException"),
	USER_ON_VOICE_CHANNEL_NOT_FOUND("jwc.exception.userOnVoiceChannelNotFoundException"),
	USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND("jwc.exception.userOnVoiceChannelWithBotNotFoundException"),
	FORBIDDEN_CHANNEL("jwc.exception.forbiddenChannelException"),
	VOLUME_UNITS_OUT_OF_BOUNDS("jwc.exception.volumeUnitsOutOfBoundsException"),
	COMMAND_IS_TURNED_OFF("jwc.exception.commandIsTurnedOffException"),
	MISMATCH_COMMAND_ARGS("jwc.exception.mismatchCommandArgumentsException"),
	VIOLATED_COMMAND_ARG_OPTIONS("jwc.exception.violatedCommandArgumentOptionsException"),
	COMMAND_AVAILABLE_ONLY_FOR_DISCRETE_TRACK("jwc.exception.commandAvailableOnlyForDiscreteTrackException"),
	RADIO_STATION_NOT_EXISTS_IS_TURNED_OFF("jwc.exception.radioStationNotExistsOrTurnedOffException"),
	RADIO_STATION_IS_NOT_PLAYING("jwc.exception.radioStationIsNotPlayingException"),
	RADIO_STATION_IS_PLAYING("jwc.exception.radioStationIsPlayingException"),
	DISCRETE_AUDIO_STREAM_IS_PLAYING("jwc.exception.discreteAudioStreamIsPlayingException"),
	UNEXPECTED_ERROR_ON_LOAD_RADIO("jwc.exception.unexpectedErrorOnLoadRadioException"),
	UNEXPECTED_ERROR_WHILE_STREAMING_RADIO("jwc.exception.unexpectedErrorWhileStreamingRadioException"),
	RADIO_STATION_NOT_PROVIDING_PLAYBACK_DATA("jwc.exception.radioStationNotProvidingPlaybackDataException"),
	;

	/**
	 * Extracts the property name from the placeholder string by retrieving the substring after the last dot for
	 * integrity checker.
	 *
	 * @return The property name derived from the placeholder.
	 */
	override val propName: String
		get() = placeholder.substring(placeholder.lastIndexOf('.') + 1)

	/**
	 * Provides a name for the integrity check module.
	 *
	 * @return The module name for integrity checking.
	 */
	override val moduleIntegrityName: String
		get() = "exceptions"
}
