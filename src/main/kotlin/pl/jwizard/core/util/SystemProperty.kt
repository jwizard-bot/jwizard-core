/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.util

import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nMiscLocale

enum class SystemProperty(
	val property: String,
	val localeSet: I18nLocale,
) {
	JVM_NAME("java.vm.name", I18nMiscLocale.JVM_NAME),
	JVM_VERSION("java.version", I18nMiscLocale.JVM_VERSION),
	JVM_SPEC_VERSION("java.vm.specification.version", I18nMiscLocale.JVM_SPEC_VERSION),
	JRE_NAME("java.runtime.name", I18nMiscLocale.JRE_NAME),
	JRE_VERSION("java.runtime.version", I18nMiscLocale.JRE_VERSION),
	JRE_SPEC_VERSION("java.specification.version", I18nMiscLocale.JRE_SPEC_VERSION),
	OS_NAME("os.name", I18nMiscLocale.OS_NAME),
	OS_ARCHITECTURE("os.arch", I18nMiscLocale.OS_ARCHITECTURE),
	;

	companion object {
		fun getAllFormatted(botConfiguration: BotConfiguration, guildId: String) = entries
			.map {
				"  `%s` :: %s".format(
					botConfiguration.i18nService.getMessage(it.localeSet, guildId),
					System.getProperty(it.property)
				)
			}
	}
}
