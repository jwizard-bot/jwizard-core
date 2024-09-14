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
	GENERAL_HEADER("jwc.system.header.general"),
	CONFIGURATION_HEADER("jwc.system.header.configuration"),
	VERSIONS_HEADER("jwc.system.header.versions"),
	JVM_HEADER("jwc.system.header.javaVirtualMachine"),
	BOT_LOCALE("jwc.system.botLocale"),
	DEPLOYMENT_DATE("jwc.system.deploymentDate"),
	DEFAULT_PREFIX("jwc.system.defaultPrefix"),
	ENABLE_SLASH_COMMANDS("jwc.system.enableSlashCommands"),
	VOTING_PERCENTAGE_RATIO("jwc.system.votingPercentageRatio"),
	VOTE_MAX_WAITING_TIME("jwc.system.voteMaxWaitingTime"),
	MUSIC_TEXT_CHANNEL("jwc.system.musicTextChannel"),
	DJ_ROLE_NAME("jwc.system.djRole"),
	MAX_REPEATS_OF_TRACK("jwc.system.maxRepeatsOfTrack"),
	LEAVE_EMPTY_CHANNEL_SEC("jwc.system.leaveEmptyChannelSec"),
	LEAVE_NO_TRACKS_SEC("jwc.system.leaveNoTracksSec"),
	DEFAULT_VOLUME("jwc.system.defaultVolume"),
	RANDOM_AUTO_CHOOSE_TRACK("jwc.system.randomAutoChooseTrack"),
	TIME_AFTER_AUTO_CHOOSE_SEC("jwc.system.timeAfterAutoChooseTrack"),
	MAX_TRACKS_TO_CHOOSE("jwc.system.maxTracksToChoose"),
	JVM_NAME("jwc.system.java.jvmName"),
	JRE_NAME("jwc.system.java.jreName"),
	JRE_VERSION("jwc.system.java.jreVersion"),
	JRE_SPEC_VERSION("jwc.system.java.jreSpecVersion"),
	JVM_XMX_MEMORY("jwc.system.jvmXmxMemory"),
	JVM_USED_MEMORY("jwc.system.jvmUsedMemory"),
	OS_NAME("jwc.system.java.osName"),
	OS_ARCHITECTURE("jwc.system.java.osArchitecture"),
	CURRENT_GUILD_OWNER_TAG("jwc.system.currentGuildOwnerTag"),
	CURRENT_GUILD_ID("jwc.system.currentGuildId"),
	JDA_VERSION("jwc.system.jdaVersion"),
	JDA_UTILITIES_VERSION("jwc.system.jdaUtilitiesVersion"),
	LAVAPLAYER_VERSION("jwc.system.lavaplayerVersion"),
	;
}
