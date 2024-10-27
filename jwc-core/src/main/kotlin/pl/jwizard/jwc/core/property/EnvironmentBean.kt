/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.property.spi.RemotePropertySupplier
import pl.jwizard.jwl.SpringKtContextFactory
import pl.jwizard.jwl.property.AppBaseProperty
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.property.PropertyNotFoundException
import kotlin.reflect.KClass

/**
 * TODO
 *
 * @property springKtContextFactory Provides access to the Spring context for retrieving beans.
 * @author Miłosz Gilga
 */
@Component
class EnvironmentBean(
	private val springKtContextFactory: SpringKtContextFactory,
) : BaseEnvironment(springKtContextFactory) {

	/**
	 * Retrieves a nullable property of type [T] for a specific guild from a property source.
	 *
	 * The property is first looked up in the database using [GuildProperty] and [guildId]. If not found, it falls back
	 * to a default property value if defined. If [allowNullable] is set to false and no value is found, a
	 * [PropertyNotFoundException] is thrown.
	 *
	 * @param T The type of the property value.
	 * @param guildProperty The guild property definition containing the database column name and type.
	 * @param guildId The ID of the guild.
	 * @param allowNullable Whether to allow null values if the property is not found.
	 * @return The property value of type [T], or null if not found and [allowNullable] is true.
	 * @throws PropertyNotFoundException If property with the given column name does not exist and [allowNullable]
	 *         is false.
	 */
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

	/**
	 * Retrieves multiple properties for a specific guild from the property source.
	 *
	 * This method fetches multiple guild-specific properties based on a list of [GuildProperty] keys. The properties are
	 * retrieved from the database, and default values are used when necessary. If no value is found for a property and
	 * no default is provided, the property will not be included in the result.
	 *
	 * @param guildProperties A list of guild-specific property definitions.
	 * @param guildId The ID of the guild for which to retrieve the properties.
	 * @return A [GuildMultipleProperties] object containing the retrieved properties.
	 */
	fun getGuildMultipleProperties(guildProperties: List<GuildProperty>, guildId: Long): GuildMultipleProperties {
		val rawProperties = remotePropertySupplier.getCombinedProperties(guildProperties.map(GuildProperty::key), guildId)
		val multipleProperties = GuildMultipleProperties(rawProperties.size)

		for ((key, nullableValue) in rawProperties) {
			val propertyKey = GuildProperty.entries.find { it.key == key } ?: continue
			val defaultProperty = try {
				BotProperty.valueOf("GUILD_${propertyKey.name}")
			} catch (_: IllegalArgumentException) {
				continue
			}
			multipleProperties[propertyKey] = nullableValue ?: getProperty<Any>(defaultProperty)
		}
		return multipleProperties
	}

	/**
	 * Retrieves a non-nullable property of type [T] for a specific guild from a property source.
	 *
	 * This method is similar to [getGuildNullableProperty], but it ensures that a value is always returned.
	 * If the property is not found in the database, it falls back to a default property value. If the default value is
	 * not found, a [PropertyNotFoundException] is thrown.
	 *
	 * @param T The type of the property value.
	 * @param guildProperty The guild property definition containing the database column name and type.
	 * @param guildId The ID of the guild.
	 * @return The property value of type [T].
	 * @throws PropertyNotFoundException If property with the given column name does not exist.
	 */
	final inline fun <reified T : Any> getGuildProperty(guildProperty: GuildProperty, guildId: Long): T =
		getGuildNullableProperty<T>(guildProperty, guildId, allowNullable = false) as T

	/**
	 * Retrieves the [RemotePropertySupplier] bean from the Spring context.
	 *
	 * This bean is used to access remote properties from the database or other external sources.
	 */
	val remotePropertySupplier: RemotePropertySupplier
		get() = springKtContextFactory.getBean(RemotePropertySupplier::class)
}
