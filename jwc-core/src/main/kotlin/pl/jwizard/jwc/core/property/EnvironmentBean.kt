package pl.jwizard.jwc.core.property

import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.property.spi.RemotePropertySupplier
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.property.AppBaseProperty
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.property.PropertyNotFoundException
import kotlin.reflect.KClass

@SingletonComponent
class EnvironmentBean(
	private val ioCKtContextFactory: IoCKtContextFactory,
) : BaseEnvironment() {
	final inline fun <reified T : Any> getGuildNullableProperty(
		guildProperty: GuildProperty,
		guildId: Long,
		allowNullable: Boolean = true,
	): T? {
		val defaultProperty = try {
			AppBaseProperty.valueOf("GUILD_${guildProperty.name}")
		} catch (_: IllegalArgumentException) {
			null
		}
		val type = defaultProperty?.type ?: guildProperty.nonDefaultType as KClass<*>
		val nullableValue = remotePropertySupplier.getProperty(guildProperty.key, guildId, type) as T?
		val value = if (nullableValue == null && defaultProperty != null) {
			getProperty(defaultProperty)
		} else {
			nullableValue
		}
		if (value == null && !allowNullable) {
			throw PropertyNotFoundException(this::class, guildProperty.key)
		}
		return value
	}

	fun getGuildMultipleProperties(
		guildProperties: List<GuildProperty>,
		guildId: Long,
	): GuildMultipleProperties {
		val rawProperties = remotePropertySupplier
			.getCombinedProperties(guildProperties.map(GuildProperty::key), guildId)
		val multipleProperties = GuildMultipleProperties(rawProperties.size)

		for ((key, nullableValue) in rawProperties) {
			val propertyKey = GuildProperty.entries.find { it.key == key } ?: continue
			val checkedProperty = if (nullableValue == null) {
				val defaultProperty = try {
					AppBaseProperty.valueOf("GUILD_${propertyKey.name}")
				} catch (_: IllegalArgumentException) {
					continue
				}
				getProperty<Any>(defaultProperty)
			} else {
				nullableValue
			}
			multipleProperties[propertyKey] = checkedProperty
		}
		return multipleProperties
	}

	final inline fun <reified T : Any> getGuildProperty(
		guildProperty: GuildProperty,
		guildId: Long,
	): T =
		getGuildNullableProperty<T>(guildProperty, guildId, allowNullable = false) as T

	// retrieve this bean as getter (DON'T PUT IN CONSTRUCTOR VIA DI)
	// must be declared as getter because it is loaded lazily
	val remotePropertySupplier: RemotePropertySupplier
		get() = ioCKtContextFactory.getBean(RemotePropertySupplier::class)
}
