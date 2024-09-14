/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import pl.jwizard.jwc.core.property.BotMultiProperty.*
import kotlin.reflect.KClass

/**
 * Enum representing different types of multi-properties used in the application.
 *
 * Defining following properties:
 *
 * - [RUNTIME_PROFILES]: Application runtime profiles. Defined all others configurations and Spring Context loaders.
 * 	 Accepted: *dev*, *prod*. Default: *empty array*.
 * - [JDA_SPLASHES_ELEMENTS]: JDA splashes elements show in sequentially order.
 * - [I18N_RESOURCES_REMOTE]: I18n CDN resources elements list. Loaded from Content Delivery Network. Must be ends
 * 	 with messages, ex. config/messages.
 * - [I18N_RESOURCES_LOCALE]: I18n local resources elements list. Loaded from classpath. Must be ends with
 * 	 messages, ex. config/messages.
 *
 * @property key The key used to retrieve the property value.
 * @property listElementsType The type of elements in the list represented by this property.
 * @property separator Optional separator used to split the list elements in a string representation.
 * @constructor Creates an instance of [BotMultiProperty].
 * @author Miłosz Gilga
 * @see BotProperty
 */
enum class BotMultiProperty(
	override val key: String,
	val listElementsType: KClass<*>,
	val separator: String?,
) : Property {

	/**
	 * Application runtime profiles. Defined all others configurations and Spring Context loaders.
	 * Accepted: *dev*, *prod*. Default: *empty array*.
	 */
	RUNTIME_PROFILES("runtime.profiles", ","),

	/**
	 * JDA splashes elements show in sequentially order.
	 */
	JDA_SPLASHES_ELEMENTS("jda.splashes.elements"),

	/**
	 * I18n CDN resources elements list. Loaded from Content Delivery Network. Must be ends with messages,
	 * ex. config/messages.
	 */
	I18N_RESOURCES_REMOTE("i18n.resources.remote"),

	/**
	 * I18n local resources elements list. Loaded from classpath. Must be ends with messages, ex. config/messages.
	 */
	I18N_RESOURCES_LOCALE("i18n.resources.local"),
	;

	/**
	 * Constructor for property with [listElementsType] field defaulting to [String::class].
	 *
	 * @param key The key used to retrieve the property value.
	 */
	constructor(key: String) : this(key, String::class, null)

	/**
	 * Constructor for property with [separator] used for splitting list elements.
	 *
	 * @param key The key used to retrieve the property value.
	 * @param separator The separator used to split list elements in the string representation.
	 */
	constructor(key: String, separator: String) : this(key, String::class, separator)
}
