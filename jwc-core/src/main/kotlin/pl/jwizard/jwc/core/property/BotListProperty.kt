/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import pl.jwizard.jwc.core.property.BotListProperty.*
import pl.jwizard.jwl.property.AppListProperty
import kotlin.reflect.KClass

/**
 * Enum representing different types of list-properties used in the application.
 *
 * - [JDA_SPLASHES_ELEMENTS]: JDA splashes elements show in sequentially order.
 * - [JDA_GATEWAY_INTENTS]: A list of [GatewayIntent] instances that are enabled for the JDA application.
 * - [JDA_CACHE_FLAGS_ENABLED]: A list of [CacheFlag] instances that are enabled for JDA caching.
 * - [JDA_CACHE_FLAGS_DISABLED]: A list of [CacheFlag] instances that are disabled for JDA caching.
 * - [JDA_SUPERUSER_PERMISSIONS]: List of JDA permissions for superuser (debug, change guild settings etc.).
 * - [LAVALINK_NODES]: Lavalink nodes definitions, where single node is:
 *   `<name>::<region group>::<node token>::<node host url>`.
 *
 * @property key The key used to retrieve the property value.
 * @property listElementsType The type of elements in the list represented by this property.
 * @property separator Optional separator used to split the list elements in a string representation.
 * @author Miłosz Gilga
 * @see BotProperty
 */
enum class BotListProperty(
	override val key: String,
	override val separator: String? = null,
	override val listElementsType: KClass<*> = String::class
) : AppListProperty {

	/**
	 * JDA splashes elements show in sequentially order.
	 */
	JDA_SPLASHES_ELEMENTS("jda.splashes.elements"),

	/**
	 * A list of [GatewayIntent] instances that are enabled for the JDA application.
	 */
	JDA_GATEWAY_INTENTS("jda.gateway-intents"),

	/**
	 * A list of [CacheFlag] instances that are enabled for JDA caching.
	 */
	JDA_CACHE_FLAGS_ENABLED("jda.cache-flags.enabled"),

	/**
	 * A list of [CacheFlag] instances that are disabled for JDA caching.
	 */
	JDA_CACHE_FLAGS_DISABLED("jda.cache-flags.disabled"),

	/**
	 * List of JDA permissions for superuser (debug, change guild settings etc.).
	 */
	JDA_SUPERUSER_PERMISSIONS("jda.superuser-permissions"),

	/**
	 * Lavalink nodes definitions, where single node is: `<name>::<region group>::<node token>::<node host url>`.
	 */
	LAVALINK_NODES("lavalink.nodes"),
	;
}
