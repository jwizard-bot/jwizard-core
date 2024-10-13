/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource

/**
 * Provides internationalization (i18n) placeholders for system-related messages.
 *
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 * @see I18nBean
 */
enum class I18nSystemSource(override val placeholder: String) : I18nLocaleSource {
	DEBUG_INFO_HEADER("jwc.system.header.debugInfo"),
	GUILD_SETTINGS_HEADER("jwc.system.header.guildSettings"),
	DEPLOYMENT_DATE("jwc.system.deploymentDate"),
	COMPILATION_VERSION("jwc.system.compilationVersion"),
	LEGACY_PREFIX("jwc.system.legacyPrefix"),
	SLASH_ENABLED("jwc.system.slashEnabled"),
	VOTING_PERCENTAGE_RATIO("jwc.system.votingPercentageRatio"),
	VOTE_MAX_WAITING_TIME("jwc.system.voteMaxWaitingTime"),
	MUSIC_TEXT_CHANNEL("jwc.system.musicTextChannel"),
	DJ_ROLE_NAME("jwc.system.djRole"),
	MIN_REPEATS_OF_TRACK("jwc.system.minRepeatsOfTrack"),
	MAX_REPEATS_OF_TRACK("jwc.system.maxRepeatsOfTrack"),
	LEAVE_EMPTY_CHANNEL_SEC("jwc.system.leaveEmptyChannelSec"),
	LEAVE_NO_TRACKS_SEC("jwc.system.leaveNoTracksSec"),
	DEFAULT_VOLUME("jwc.system.defaultVolume"),
	RANDOM_AUTO_CHOOSE_TRACK("jwc.system.randomAutoChooseTrack"),
	TIME_AFTER_AUTO_CHOOSE_SEC("jwc.system.timeAfterAutoChooseTrack"),
	MAX_TRACKS_TO_CHOOSE("jwc.system.maxTracksToChoose"),
	JVM_NAME("jwc.system.java.jvmName"),
	JRE_VERSION("jwc.system.java.jreVersion"),
	JVM_MEMORY_USAGE("jwc.system.java.jvmMemoryUsage"),
	JVM_XMX_MEMORY("jwc.system.java.jvmXmxMemory"),
	JVM_USED_MEMORY("jwc.system.java.jvmUsedMemory"),
	OS_NAME("jwc.system.java.osName"),
	OS_ARCHITECTURE("jwc.system.java.osArchitecture"),
	JDA_VERSION("jwc.system.jdaVersion"),
	LAVALINK_CLIENT_VERSION("jwc.system.lavalinkClientVersion"),
	AVAILABLE_LAVALINK_NODES("jwc.system.availableLavalinkNodes"),
	;
}
