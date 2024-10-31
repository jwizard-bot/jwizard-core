/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwl.i18n.I18nLocaleSource

/**
 * Provides internationalization (i18n) placeholders for system-related messages.
 *
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 */
enum class I18nSystemSource(override val placeholder: String) : I18nLocaleSource {
	DEBUG_INFO_HEADER("jw.system.header.debugInfo"),
	GUILD_SETTINGS_HEADER("jw.system.header.guildSettings"),
	DEPLOYMENT_DATE("jw.system.deploymentDate"),
	COMPILATION_VERSION("jw.system.compilationVersion"),
	LEGACY_PREFIX("jw.system.legacyPrefix"),
	SLASH_ENABLED("jw.system.slashEnabled"),
	LANGUAGE_TAG("jw.system.languageTag"),
	VOTING_PERCENTAGE_RATIO("jw.system.votingPercentageRatio"),
	VOTE_MAX_WAITING_TIME("jw.system.voteMaxWaitingTime"),
	MUSIC_TEXT_CHANNEL("jw.system.musicTextChannel"),
	DJ_ROLE_NAME("jw.system.djRole"),
	MIN_REPEATS_OF_TRACK("jw.system.minRepeatsOfTrack"),
	MAX_REPEATS_OF_TRACK("jw.system.maxRepeatsOfTrack"),
	LEAVE_EMPTY_CHANNEL_SEC("jw.system.leaveEmptyChannelSec"),
	LEAVE_NO_TRACKS_SEC("jw.system.leaveNoTracksSec"),
	PLAYER_VOLUME("jw.system.playerVolume"),
	RANDOM_AUTO_CHOOSE_TRACK("jw.system.randomAutoChooseTrack"),
	TIME_AFTER_AUTO_CHOOSE_SEC("jw.system.timeAfterAutoChooseTrack"),
	MAX_TRACKS_TO_CHOOSE("jw.system.maxTracksToChoose"),
	JVM_NAME("jw.system.java.jvmName"),
	JRE_VERSION("jw.system.java.jreVersion"),
	JVM_MEMORY_USAGE("jw.system.java.jvmMemoryUsage"),
	JVM_XMX_MEMORY("jw.system.java.jvmXmxMemory"),
	JVM_USED_MEMORY("jw.system.java.jvmUsedMemory"),
	OS_NAME("jw.system.java.osName"),
	OS_ARCHITECTURE("jw.system.java.osArchitecture"),
	JDA_VERSION("jw.system.jdaVersion"),
	LAVALINK_CLIENT_VERSION("jw.system.lavalinkClientVersion"),
	AVAILABLE_LAVALINK_NODES("jw.system.availableLavalinkNodes"),
	;
}
