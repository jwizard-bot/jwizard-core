/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.core.env.PropertySourcesPropertyResolver
import org.springframework.core.env.StandardEnvironment
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.SpringKtContextFactory
import pl.jwizard.jwc.core.property.extractor.EnvPropertyValueExtractor
import pl.jwizard.jwc.core.property.extractor.VaultPropertyValueExtractor
import pl.jwizard.jwc.core.property.loader.DbPropertySourceLoader
import pl.jwizard.jwc.core.property.loader.YamlPropertySourceLoader
import pl.jwizard.jwc.core.property.spi.RemotePropertySupplier
import pl.jwizard.jwc.core.util.KtCast
import kotlin.reflect.KClass

/**
 * Component responsible for loading and resolving properties from various sources, including YAML files, environment
 * variables, and Vault secrets.
 *
 * This class initializes and configures property sources, including YAML configuration files, environment variables,
 * and Vault secrets. It also provides methods for retrieving and casting property values from these sources.
 *
 * @author Miłosz Gilga
 */
@Component
class EnvironmentBean(private val springKtContextFactory: SpringKtContextFactory) {

	companion object {
		private val log = LoggerFactory.getLogger(EnvironmentBean::class.java)
	}

	/**
	 * The [StandardEnvironment] instance used to manage and access property sources.
	 *
	 * This environment provides the context in which property sources are registered and managed. It allows access
	 * to properties from various sources such as YAML files, environment variables, and Vault secrets.
	 */
	private final val environment = StandardEnvironment()

	/**
	 * The [PropertySourcesPropertyResolver] instance used to resolve property values from the environment's property
	 * sources.
	 *
	 * This resolver provides methods to retrieve property values based on the keys. It is created and updated
	 * whenever a new property source is added to the environment.
	 */
	final lateinit var propertySourceResolver: PropertySourcesPropertyResolver
		private set

	/**
	 * Initializes the property sources and loads properties from YAML files, environment variables, Vault, and database.
	 *
	 * This method is annotated with [PostConstruct] to ensure it is executed after the [RemotePropertySupplier] bean
	 * has been created.
	 */
	@PostConstruct
	fun afterConstruct() {
		val propertiesEnv = PropertiesEnvironment(environment.propertySources)

		propertySourceResolver = propertiesEnv.resolver

		val runtimeProfiles = getMultiProperty<String>(BotMultiProperty.RUNTIME_PROFILES)
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
	 * @param botMultiProperty The multi-property definition containing the key and element type.
	 * @return A list of properties of type [T].
	 */
	final inline fun <reified T> getMultiProperty(botMultiProperty: BotMultiProperty): List<T> {
		val elements = mutableListOf<String>()
		if (botMultiProperty.separator == null) {
			var listIndex = 0
			while (true) {
				val listElement = propertySourceResolver.getProperty("${botMultiProperty.key}[${listIndex++}]")
					?: break
				elements.add(listElement)
			}
		} else {
			val rawValues = propertySourceResolver.getProperty(botMultiProperty.key)
				?: throw PropertyNotFoundException(this::class, botMultiProperty.key)
			rawValues.split(botMultiProperty.separator).forEach { elements.add(it.trim()) }
		}
		if (elements.isEmpty()) {
			throw PropertyNotFoundException(this::class, botMultiProperty.key)
		}
		return elements.map { KtCast.castToValue(it, botMultiProperty.listElementsType) }
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
		val rawValue = propertySourceResolver.getProperty(botProperty.key)
			?: throw PropertyNotFoundException(this::class, botProperty.key)
		return KtCast.castToValue(rawValue, botProperty.type)
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
	 * 				 is false.
	 */
	final inline fun <reified T : Any> getGuildNullableProperty(
		guildProperty: GuildProperty,
		guildId: String,
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
	final inline fun <reified T : Any> getGuildProperty(guildProperty: GuildProperty, guildId: String): T =
		getGuildNullableProperty<T>(guildProperty, guildId, allowNullable = false) as T

	/**
	 * Retrieves the [RemotePropertySupplier] bean from the Spring context.
	 *
	 * This bean is used to access remote properties from the database or other external sources.
	 */
	val remotePropertySupplier: RemotePropertySupplier
		get() = springKtContextFactory.getBean(RemotePropertySupplier::class)
}
