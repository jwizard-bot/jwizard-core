/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jvm

import pl.jwizard.jwc.core.i18n.source.I18nSystemSource
import pl.jwizard.jwc.core.property.BotProperty

/**
 * Enum representing various system properties such as JVM, JRE, and OS details. Each enum constant maps a system
 * property from [BotProperty] to its localized equivalent from [I18nSystemSource].
 *
 * @property botProperty The system property to retrieve (ex. JVM name, OS name).
 * @property i18nSystemSource The internationalized source for the property name.
 * @author Miłosz Gilga
 */
enum class SystemProperty(
	val botProperty: BotProperty,
	val i18nSystemSource: I18nSystemSource,
) {

	/**
	 * Retrieves the name of the Java Virtual Machine (JVM) being used.
	 */
	JVM_NAME(BotProperty.JVM_NAME, I18nSystemSource.JVM_NAME),

	/**
	 * Retrieves the name of the Java Runtime Environment (JRE).
	 */
	JRE_NAME(BotProperty.JRE_NAME, I18nSystemSource.JRE_NAME),

	/**
	 * Retrieves the version of the Java Runtime Environment (JRE).
	 */
	JRE_VERSION(BotProperty.JRE_VERSION, I18nSystemSource.JRE_VERSION),

	/**
	 * Retrieves the specification version of the Java Runtime Environment (JRE).
	 */
	JRE_SPEC_VERSION(BotProperty.JRE_SPEC_VERSION, I18nSystemSource.JRE_SPEC_VERSION),

	/**
	 * Retrieves the name of the operating system.
	 */
	OS_NAME(BotProperty.OS_NAME, I18nSystemSource.OS_NAME),

	/**
	 * Retrieves the architecture of the operating system.
	 */
	OS_ARCHITECTURE(BotProperty.OS_ARCHITECTURE, I18nSystemSource.OS_ARCHITECTURE),
	;
}
