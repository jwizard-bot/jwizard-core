/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import jakarta.annotation.PostConstruct
import org.springframework.core.env.StandardEnvironment
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.extractor.EnvPropertyValueExtractor
import pl.jwizard.jwc.core.property.extractor.VaultPropertyValueExtractor
import pl.jwizard.jwc.core.property.loader.DbPropertySourceLoader
import pl.jwizard.jwc.core.property.loader.YamlPropertySourceLoader
import pl.jwizard.jwc.core.property.spi.RemotePropertySupplier
import pl.jwizard.jwc.core.util.castToValue
import pl.jwizard.jwl.SpringKtContextFactory
import pl.jwizard.jwl.util.logger
import kotlin.reflect.KClass

/**
 * Component responsible for loading and resolving properties from various sources, including YAML files, environment
 * variables, and Vault secrets.
 *
 * This class initializes and configures property sources, including YAML configuration files, environment variables,
 * and Vault secrets. It also provides methods for retrieving and casting property values from these sources.
 *
 * @property springKtContextFactory Provides access to the Spring context for retrieving beans.
 * @author Miłosz Gilga
 */
@Component
class EnvironmentBean(private val springKtContextFactory: SpringKtContextFactory) {

	companion object {
		private val log = logger<EnvironmentBean>()
	}

	/**
	 * The [StandardEnvironment] instance used to manage and access property sources.
	 *
	 * This environment provides the context in which property sources are registered and managed. It allows access
	 * to properties from various sources such as YAML files, environment variables, and Vault secrets.
	 */
	private final val environment = StandardEnvironment()

	/**
	 * An instance of [PropertiesEnvironment] that manages and resolves property sources.
	 */
	final lateinit var propertiesEnv: PropertiesEnvironment
		private set

	/**
	 * Initializes the property sources and loads properties from YAML files, environment variables, Vault, and database.
	 *
	 * This method is annotated with [PostConstruct] to ensure it is executed after the [RemotePropertySupplier] bean
	 * has been created.
	 */
	@PostConstruct
	fun afterConstruct() {
		propertiesEnv = PropertiesEnvironment(environment.propertySources)
		propertiesEnv.createResolver()

		val runtimeProfiles = getListProperty<String>(BotListProperty.RUNTIME_PROFILES)
		propertiesEnv.addSource(YamlPropertySourceLoader(runtimeProfiles))
		log.info("Loaded runtime profiles: {}", runtimeProfiles)

		val envFileEnabled = getProperty<Boolean>(BotProperty.ENV_ENABLED)
		propertiesEnv.addSource(EnvPropertyValueExtractor(envFileEnabled))

		propertiesEnv.addSource(
			VaultPropertyValueExtractor(
				vaultServerUri = getProperty(BotProperty.VAULT_URL),
				vaultToken = getProperty(BotProperty.VAULT_TOKEN),
				vaultKvBackend = getProperty(BotProperty.VAULT_KV_BACKEND),
				vaultKvDefaultContext = getProperty(BotProperty.VAULT_KV_DEFAULT_CONTEXT),
				vaultKvApplicationName = getProperty(BotProperty.VAULT_KV_APPLICATION_NAME),
			)
		)
		propertiesEnv.addSource(DbPropertySourceLoader(remotePropertySupplier))
		log.info("Load: {} properties from sources: {}.", propertiesEnv.size, propertiesEnv.propertySourceNames)
	}

	/**
	 * Retrieves a list of properties of type [T] from a multi-property source.
	 *
	 * The properties are retrieved from a source where the properties are indexed by a key suffix. If no properties are
	 * found, a [PropertyNotFoundException] is thrown.
	 *
	 * @param T The type of the property values in the list.
	 * @param botListProperty The multi-property definition containing the key and element type.
	 * @return A list of properties of type [T].
	 */
	final inline fun <reified T> getListProperty(botListProperty: BotListProperty): List<T> {
		val elements = mutableListOf<String>()
		val resolver = propertiesEnv.resolver
		if (botListProperty.separator == null) {
			var listIndex = 0
			while (true) {
				val listElement = resolver.getProperty("${botListProperty.key}[${listIndex++}]")
					?: break
				elements.add(listElement)
			}
		} else {
			val rawValues = resolver.getProperty(botListProperty.key)
				?: throw PropertyNotFoundException(this::class, botListProperty.key)
			rawValues.split(botListProperty.separator).forEach { elements.add(it.trim()) }
		}
		if (elements.isEmpty()) {
			throw PropertyNotFoundException(this::class, botListProperty.key)
		}
		return elements.map { castToValue(it, botListProperty.listElementsType) }
	}

	/**
	 * Retrieves a single property of type [T] from a property source.
	 *
	 * The property is retrieved based on the provided [BotProperty] key. If the property is not found, a
	 * [PropertyNotFoundException] is thrown.
	 *
	 * @param T The type of the property value.
	 * @param botProperty The property definition containing the key and type.
	 * @return The property value of type [T].
	 * @throws PropertyNotFoundException If property with following key not exist.
	 */
	final inline fun <reified T : Any> getProperty(botProperty: BotProperty): T {
		val rawValue = propertiesEnv.resolver.getProperty(botProperty.key)
			?: throw PropertyNotFoundException(this::class, botProperty.key)
		return castToValue(rawValue, botProperty.type)
	}

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
			BotProperty.valueOf("GUILD_${guildProperty.name}")
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
