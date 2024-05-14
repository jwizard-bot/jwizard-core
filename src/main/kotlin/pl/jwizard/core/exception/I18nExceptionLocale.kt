/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.exception

import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nMiscLocale
import java.util.*

enum class I18nExceptionLocale(
	private val placeholder: String,
	private val bugTracker: Int
) : I18nLocale {

	// common
	UNEXPECTED_EXCEPTION("pl.jwizard.exception.unexpectedException", 0),
	MODULE_IS_TURNED_OFF("pl.jwizard.exception.moduleIsTurnedOffException", 2),

	// audio player
	TRACK_OFFSET_OUT_OF_BOUNDS("pl.jwizard.exception.trackOffsetOutOfBoundsException", 100),
	TRACK_THE_SAME_POSITION("pl.jwizard.exception.trackTheSamePositionException", 101),
	TRACK_QUEUE_IS_EMPTY("pl.jwizard.exception.trackQueueIsEmptyException", 102),
	TRACK_IS_NOT_PLAYING("pl.jwizard.exception.trackIsNotPlayingException", 103),
	INVOKER_IS_NOT_TRACK_SENDER_OR_ADMIN("pl.jwizard.exception.invokerIsNotTrackSenderOrAdminException", 104),
	USER_NOT_ADDED_TRACKS_TO_QUEUE("pl.jwizard.exception.userNotAddedTracksToQueueException", 105),
	TRACK_IS_NOT_PAUSED("pl.jwizard.exception.trackIsNotPausedException", 106),
	ACTIVE_MUSIC_PLAYING_NOT_FOUND("pl.jwizard.exception.activeMusicPlayingNotFoundException", 107),
	FORBIDDEN_TEXT_CHANNEL("pl.jwizard.exception.forbiddenTextChannelException", 108),
	LOCK_COMMAND_ON_TEMPORARY_HALTED("pl.jwizard.exception.lockCommandOnTemporaryHaltedException", 109),
	TRACK_REPEATS_OUT_OF_BOUNDS("pl.jwizard.exception.trackRepeatsOutOfBoundsException", 110),
	MAX_REPEATS_OUT_OF_BOUNDS("pl.jwizard.exception.maxRepeatsOutOfBoundsException", 111),
	ISSUE_WHILE_PLAYING_TRACK("pl.jwizard.exception.unexpectedErrorOnPlayTrack", 112),
	ISSUE_WHILE_LOADING_TRACK("pl.jwizard.exception.unexpectedErrorOnLoadTrack", 113),
	NOT_FOUND_TRACK("pl.jwizard.exception.notFoundAudioTrack", 114),

	// user
	USER_ID_ALREADY_WITH_BOT("pl.jwizard.exception.userIsAlreadyWithBotException", 200),
	USER_NOT_FOUND_IN_GUILD("pl.jwizard.exception.userNotFoundInGuildException", 201),
	UNAUTHORIZED_DJ("pl.jwizard.exception.unauthorizedDjException", 202),
	UNAUTHORIZED_DJ_OR_SENDER("pl.jwizard.exception.unauthorizedDjOrSenderException", 203),
	UNAUTHORIZED_MANAGER("pl.jwizard.exception.unauthorizedManagerException", 204),
	USER_ON_VOICE_CHANNEL_NOT_FOUND("pl.jwizard.exception.userOnVoiceChannelNotFoundException", 205),
	USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND("pl.jwizard.exception.userOnVoiceChannelWithBotNotFoundException", 206),

	// command
	USED_COMMAND_ON_FORBIDDEN_CHANNEL("pl.jwizard.exception.usedCommandOnForbiddenChannelException", 301),
	VOLUME_UNITS_OUT_OF_BOUNDS("pl.jwizard.exception.volumeUnitsOutOfBoundsException", 302),
	COMMAND_IS_TURNED_OFF("pl.jwizard.exception.commandIsTurnedOffException", 303),
	MISMATCH_COMMAND_ARGS("pl.jwizard.exception.mismatchCommandArgumentsException", 304),
	;

	override fun getPlaceholder() = placeholder

	fun createBugTrackerMessage(botConfiguration: BotConfiguration, guildId: String): String {
		val createMessage: (holder: I18nLocale) -> String = { botConfiguration.i18nService.getMessage(it, guildId) }
		val (buildVersion) = botConfiguration.botProperties.deployment
		return StringJoiner("")
			.add("${createMessage(I18nMiscLocale.BUG_TRACKER)}: `$bugTracker`")
			.add("\n")
			.add("${createMessage(I18nMiscLocale.COMPILATION_VERSION)}: `${buildVersion}`")
			.toString()
	}
}
