package pl.jwizard.jwc.core.property

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.property.spi.RemotePropertySupplier
import pl.jwizard.jwl.property.AppBaseProperty
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.property.PropertyNotFoundException
import kotlin.reflect.KClass

@Component
class GuildEnvironment(
	val environment: BaseEnvironment,
	val remotePropertySupplier: RemotePropertySupplier,
) {
	final inline fun <reified T : Any> getGuildProperty(
		guildProperty: GuildProperty,
		guildId: Long,
	): T = getGuildNullableProperty<T>(guildProperty, guildId, allowNullable = false) as T

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
			environment.getProperty(defaultProperty)
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
				environment.getProperty<Any>(defaultProperty)
			} else {
				nullableValue
			}
			multipleProperties[propertyKey] = checkedProperty
		}
		return multipleProperties
	}
}
