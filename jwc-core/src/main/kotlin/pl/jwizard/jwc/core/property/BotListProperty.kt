package pl.jwizard.jwc.core.property

import pl.jwizard.jwl.property.AppListProperty
import kotlin.reflect.KClass

enum class BotListProperty(
	override val key: String,
	override val separator: String? = null,
	override val listElementsType: KClass<*> = String::class
) : AppListProperty {
	// JDA splashes elements show in sequentially order
	JDA_SPLASHES_ELEMENTS("jda.splashes.elements"),

	// a list of [GatewayIntent] instances that are enabled for the JDA application
	JDA_GATEWAY_INTENTS("jda.gateway-intents"),

	// a list of [CacheFlag] instances that are enabled for JDA caching
	JDA_CACHE_FLAGS_ENABLED("jda.cache-flags.enabled"),

	// a list of [CacheFlag] instances that are disabled for JDA caching
	JDA_CACHE_FLAGS_DISABLED("jda.cache-flags.disabled"),

	// list of JDA permissions for superuser
	JDA_SUPERUSER_PERMISSIONS("jda.superuser-permissions"),
	;
}
