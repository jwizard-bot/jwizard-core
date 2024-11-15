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
	SHARDS_INFO_HEADER("jw.system.header.shardsInfoHeader"),
	DEPLOYMENT_DATE("jw.system.deploymentDate"),
	COMPILATION_VERSION("jw.system.compilationVersion"),
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
	SHARDS_START_OFFSET("jw.system.shardsStartOffset"),
	SHARDS_END_OFFSET("jw.system.shardsEndOffset"),
	SHARDS_OFFSET_LENGTH("jw.system.shardsOffsetLength"),
	QUEUED_SHARDS("jw.system.queuedShards"),
	RUNNING_SHARDS("jw.system.runningShards"),
	AVG_GATEWAY_PING("jw.system.avgGatewayPing"),
	;
}
