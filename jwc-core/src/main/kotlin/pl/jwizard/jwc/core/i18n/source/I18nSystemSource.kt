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
	JVM_MEMORY_USAGE("jw.system.java.jvmMemoryUsage"),
	JVM_XMX_MEMORY("jw.system.java.jvmXmxMemory"),
	JVM_USED_MEMORY("jw.system.java.jvmUsedMemory"),
	AVAILABLE_AUDIO_NODES("jw.system.availableAudioNodes"),
	;
}
