/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property.loader

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.ClassPathResource
import pl.jwizard.jwc.core.property.PropertySourceData
import kotlin.reflect.jvm.jvmName

/**
 * Loader for YAML property sources.
 *
 * @property runtimeProfiles List of runtime modes to load specific YAML configuration files.
 * @author Miłosz Gilga
 * @see PropertySourceData
 */
class YamlPropertySourceLoader(
	private val runtimeProfiles: List<String>,
) : PropertySourceData<YamlPropertySourceLoader>(YamlPropertySourceLoader::class) {

	/**
	 * The property source created from the loaded properties.
	 */
	private val propertySource: PropertiesPropertySource =
		PropertiesPropertySource(YamlPropertySourceLoader::class.jvmName, properties)

	companion object {
		private val log = LoggerFactory.getLogger(YamlPropertySourceLoader::class.java)

		/**
		 * Default prefix for YAML configuration files.
		 */
		private const val DEFAULT_YAML_PREFIX = "config/application"

		/**
		 * Available YAML file extensions.
		 */
		private val YAML_EXTENSIONS = arrayOf("yaml", "yml")
	}

	/**
	 * Loads properties from YAML configuration files.
	 *
	 * This method reads properties from YAML files located on the classpath. It loads a default YAML file and additional
	 * files based on runtime modes. The files are searched with `.yaml` and `.yml` extensions.
	 *
	 * Logs the names of the YAML files and the number of properties loaded.
	 *
	 * @return A map of properties where keys are property names and values are property values. Returns an empty map
	 * 				 if no properties are found.
	 */
	override fun setProperties(): Map<Any, Any> {
		val yamlPropertiesFactoryBean = YamlPropertiesFactoryBean()

		val defaultProperties = getExistingYamlFile()
		val runtimeProperties = runtimeProfiles.map { getExistingYamlFile(".$it") }

		val fileContents = mutableListOf<ClassPathResource>()
		defaultProperties?.let { fileContents.add(it) }
		runtimeProperties.forEach { propertiesMode -> propertiesMode?.let { fileContents.add(it) } }

		yamlPropertiesFactoryBean.setResources(*fileContents.toTypedArray())
		log.info("Load YAML configuration files: {}.", fileContents.map { it.filename })

		val properties = yamlPropertiesFactoryBean.getObject()
		return properties?.map { it.key to it.value }?.toMap() ?: emptyMap()
	}

	/**
	 * Retrieves the value of the specified property name from the loaded properties.
	 *
	 * @param name The property name.
	 * @return The property value, or null if not found.
	 */
	override fun getProperty(name: String): Any? = properties.getProperty(name)

	override fun getSourceLoader(): PropertySource<*> = propertySource

	/**
	 * Finds an existing YAML configuration file with the given suffix.
	 *
	 * @param suffix The suffix to append to the default YAML file prefix.
	 * @return A [ClassPathResource] for the existing YAML file, or null if not found.
	 */
	private fun getExistingYamlFile(suffix: String = ""): ClassPathResource? = YAML_EXTENSIONS
		.map { ClassPathResource("$DEFAULT_YAML_PREFIX$suffix.$it") }
		.firstOrNull { it.exists() }
}
