/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.i18n

enum class I18nMiscLocale(
	private val placeholder: String
) : I18nLocale {

	// utils
	REQUIRED("pl.jwizard.util.requred"),
	OPTIONAL("pl.jwizard.util.optional"),
	BUG_TRACKER("pl.jwizard.util.bugTracker"),
	COMPILATION_VERSION("pl.jwizard.util.compilationVersion"),
	TURN_ON("pl.jwizard.util.turnOn"),
	TURN_OFF("pl.jwizard.util.turnOff"),

	// audio player
	COUNT_OF_TRACKS("pl.jwizard.audio.countOfTracks"),
	TRACKS_TOTAL_DURATION_TIME("pl.jwizard.audio.totalDurationTime"),
	NEXT_TRACK_INDEX_MESS("pl.jwizard.audio.nextTrackIndex"),
	TRACK_ADDDED_BY("pl.jwizard.audio.addedBy"),
	TRACK_NAME("pl.jwizard.audio.trackName"),
	TRACK_DURATION_TIME("pl.jwizard.audio.durationTime"),
	TRACK_POSITION_IN_QUEUE("pl.jwizard.audio.positionInQueue"),
	CURRENT_PLAYING_TRACK("pl.jwizard.audio.currentPlayingTrackDesc"),
	CURRENT_PAUSED_TRACK("pl.jwizard.audio.currentPausedTrackDesc"),
	CURRENT_PLAYING_TIMESTAMP("pl.jwizard.audio.currentPlayingTimestamp"),
	CURRENT_PAUSED_TIMESTAMP("pl.jwizard.audio.currentPausedTimestamp"),
	CURRENT_TRACK_LEFT_TO_NEXT("pl.jwizard.audio.currentTrackLeftToNext"),
	PAUSED_TRACK_TIME("pl.jwizard.audio.pausedTrackTime"),
	PAUSED_TRACK_ESTIMATE_TIME("pl.jwizard.audio.pausedTrackEstimateTime"),
	PAUSED_TRACK_TOTAL_DURATION("pl.jwizard.audio.pausedTrackTotalDuration"),
	ALL_TRACKS_IN_QUEUE_COUNT("pl.jwizard.audio.allTracksInQueueCount"),
	ALL_TRACKS_IN_QUEUE_DURATION("pl.jwizard.audio.allTracksInQueueDuration"),
	APPROX_TO_NEXT_TRACK_FROM_QUEUE("pl.jwizard.audio.approxToNextTrackFromQueue"),
	PLAYLIST_AVERAGE_TRACK_DURATION("pl.jwizard.audio.playlistAverageTrackDuration"),
	PLAYLIST_REPEATING_MODE("pl.jwizard.audio.playlistRepeatingMode"),

	// voting
	ON_SUCCESS_VOTING("pl.jwizard.voting.votingSuccess"),
	ON_FAILURE_VOTING("pl.jwizard.voting.votingFailure"),
	ON_TIMEOUT_VOTING("pl.jwizard.voting.votingTimeout"),
	VOTES_FOR_YES_NO_VOTING("pl.jwizard.voting.votesForYesNo"),
	REQUIRED_TOTAL_VOTES_VOTING("pl.jwizard.voting.requiredTotalVotes"),
	VOTES_RATIO_VOTING("pl.jwizard.voting.votesRatio"),
	MAX_TIME_VOTING("pl.jwizard.voting.maxVotingTime"),
	TOO_FEW_POSITIVE_VOTES_VOTING("pl.jwizard.voting.tooFewPositiveVotes"),
	FIRST_RESULT("pl.jwizard.voting.firstResult"),
	RANDOM_RESULT("pl.jwizard.voting.randomResult"),

	// system
	GENERAL_HEADER("pl.jwizard.system.header.general"),
	CONFIGURATION_HEADER("pl.jwizard.system.header.configuration"),
	VERSIONS_HEADER("pl.jwizard.system.header.versions"),
	JVM_HEADER("pl.jwizard.system.header.javaVirtualMachine"),
	BOT_LOCALE("pl.jwizard.system.botLocale"),
	DEFAULT_PREFIX("pl.jwizard.system.defaultPrefix"),
	ENABLE_SLASH_COMMANDS("pl.jwizard.system.enableSlashCommands"),
	VOTE_MAX_WAITING_TIME("pl.jwizard.system.voteMaxWaitingTime"),
	LEAVE_CHANNEL_WAITING_TIME("pl.jwizard.system.leaveChannelWaitingTime"),
	JVM_NAME("pl.jwizard.system.java.jvmName"),
	JVM_VERSION("pl.jwizard.system.java.jvmVersion"),
	JVM_SPEC_VERSION("pl.jwizard.system.java.jvmSpecVersion"),
	JRE_NAME("pl.jwizard.system.java.jreName"),
	JRE_VERSION("pl.jwizard.system.java.jreVersion"),
	JRE_SPEC_VERSION("pl.jwizard.system.java.jreSpecVersion"),
	JVM_XMX_MEMORY("pl.jwizard.system.jvmXmxMemory"),
	JVM_USED_MEMORY("pl.jwizard.system.jvmUsedMemory"),
	OS_NAME("pl.jwizard.system.java.osName"),
	OS_ARCHITECTURE("pl.jwizard.system.java.osArchitecture"),
	CURRENT_GUILD_OWNER_TAG("pl.jwizard.system.currentGuildOwnerTag"),
	CURRENT_GUILD_ID("pl.jwizard.system.currentGuildId"),
	JDA_VERSION("pl.jwizard.system.jdaVersion"),
	JDA_UTILITIES_VERSION("pl.jwizard.system.jdaUtilitiesVersion"),
	LAVAPLAYER_VERSION("pl.jwizard.system.lavaplayerVersion"),

	// action elements
	REFRESH_BUTTON("pl.jwizard.action.refreshButton")
	;

	override fun getPlaceholder() = placeholder
}
