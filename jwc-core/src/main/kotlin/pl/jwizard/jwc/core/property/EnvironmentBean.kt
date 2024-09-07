/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import org.slf4j.LoggerFactory
import org.springframework.core.env.PropertySourcesPropertyResolver
import org.springframework.core.env.StandardEnvironment
import org.springframework.stereotype.Component
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
class EnvironmentBean {

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
	private final var propertySourceResolver: PropertySourcesPropertyResolver

	init {
		propertySourceResolver = createResolver()

		val runtimeProfiles = getMultiProperty<String>(BotMultiProperty.RUNTIME_PROFILES)
		initPropertySourceLoader(YamlPropertySourceLoader(runtimeProfiles))
		log.info("Loaded runtime profiles: {}", runtimeProfiles)

		val envFileEnabled = getProperty<Boolean>(BotProperty.ENV_ENABLED)
		initPropertySourceLoader(EnvPropertyValueExtractor(envFileEnabled))

		initPropertySourceLoader(
			VaultPropertyValueExtractor(
				vaultServerUri = getProperty(BotProperty.VAULT_URL),
				vaultToken = getProperty(BotProperty.VAULT_TOKEN),
				vaultKvBackend = getProperty(BotProperty.VAULT_KV_BACKEND),
				vaultKvDefaultContext = getProperty(BotProperty.VAULT_KV_DEFAULT_CONTEXT),
				vaultKvApplicationName = getProperty(BotProperty.VAULT_KV_APPLICATION_NAME),
			)
		)
	}

	companion object {
		private val log = LoggerFactory.getLogger(EnvironmentBean::class.java)
	}

	/**
	 * Initializes the property source loader with the given [loader].
	 *
	 * This method loads properties using the provided loader and adds the loaded property source to the environment.
	 * After adding the property source, it recreates the [propertySourceResolver] to reflect the new properties.
	 *
	 * @param T The type of property values handled by the loader.
	 * @param loader The [PropertySourceData] instance responsible for loading properties.
	 */
	private fun <T> initPropertySourceLoader(loader: PropertySourceData<T>) {
		loader.loadProperties()
		environment.propertySources.addLast(loader.getSourceLoader())
		propertySourceResolver = createResolver()
	}

	/**
	 * Creates and returns a new [PropertySourcesPropertyResolver] instance.
	 *
	 * This resolver is used to retrieve property values from the environment's property sources.
	 *
	 * @return A [PropertySourcesPropertyResolver] instance.
	 */
	private fun createResolver() = PropertySourcesPropertyResolver(environment.propertySources)

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
		val resolver = getPropertyResourceResolver()
		val elements = mutableListOf<String>()
		if (botMultiProperty.separator == null) {
			var listIndex = 0
			while (true) {
				val listElement = resolver.getProperty("${botMultiProperty.key}[${listIndex++}]")
					?: break
				elements.add(listElement)
			}
		} else {
			val rawValues = resolver.getProperty(botMultiProperty.key)
				?: throw PropertyNotFoundException(this::class, botMultiProperty.key)
			rawValues.split(botMultiProperty.separator).forEach { elements.add(it.trim()) }
		}
		if (elements.isEmpty()) {
			throw PropertyNotFoundException(this::class, botMultiProperty.key)
		}
		return elements.map { castToValue(it, botMultiProperty.listElementsType) }
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
	 */
	final inline fun <reified T : Any> getProperty(botProperty: BotProperty): T {
		val resolver = getPropertyResourceResolver()
		val rawValue = resolver.getProperty(botProperty.key)
			?: throw PropertyNotFoundException(this::class, botProperty.key)
		return castToValue(rawValue, botProperty.type)
	}

	/**
	 * Casts a string value to the specified type [T].
	 *
	 * The string value is cast to the target type based on the provided [KClass].
	 *
	 * @param T The target type to cast the value to.
	 * @param value The string value to cast.
	 * @param targetType The [KClass] representing the target type.
	 * @return The cast value of type [T].
	 */
	final inline fun <reified T : Any> castToValue(value: String, targetType: KClass<*>): T {
		return when (targetType) {
			Int::class -> value.toInt() as T
			Double::class -> value.toDouble() as T
			Boolean::class -> value.toBoolean() as T
			else -> value as T
		}
	}

	/**
	 * Provides access to the property source resolver.
	 *
	 * @return The [PropertySourcesPropertyResolver] instance.
	 */
	fun getPropertyResourceResolver() = propertySourceResolver
}
