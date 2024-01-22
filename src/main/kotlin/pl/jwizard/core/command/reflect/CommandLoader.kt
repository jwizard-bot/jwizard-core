/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

import kotlin.system.exitProcess
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.http.HttpClient
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component

@Component
class CommandLoader(
	private val _applicationContext: ApplicationContext,
	private val _httpClient: HttpClient,
) {
	private final val _commandsProxyContainer = mutableMapOf<String, CommandProxyData>()
	private final val _categoriesProxyContainer = mutableMapOf<String, String>()

	private var _scanner = ClassPathScanningCandidateComponentProvider(false)

	init {
		_scanner.addIncludeFilter(AnnotationTypeFilter(CommandListenerBean::class.java))
	}

	fun fetchCommandsFromApi() {
		val urlSuffix = "command/all"
		val (categories, commands) = _httpClient
			.makeGetRequest(urlSuffix, CommandsResDto::class.java, true) ?: return

		categories.forEach { (key, value) -> _categoriesProxyContainer[key] = value }
		commands.forEach { (key, value) -> _commandsProxyContainer[key] = CommandProxyData(value) }

		LOG.info("Fetched {} command categories from API: {}", categories.size, _httpClient.mergeWithHostUrl(urlSuffix))
		LOG.info("Fetched {} commands from API: {}", commands.size, _httpClient.mergeWithHostUrl(urlSuffix))
	}

	fun reflectAndLoadCommands() {
		val reflectCommands = _scanner
			.findCandidateComponents(SCANNING_BASE_PACKAGE)
			.map {
				val clazz = Class.forName(it.beanClassName)
				val annotation = clazz.getAnnotation(CommandListenerBean::class.java)
				annotation to clazz
			}
			.filter { (annotation, _) -> _commandsProxyContainer.containsKey(annotation.id) }
			.associate { (annotation, clazz) ->
				annotation.id to _applicationContext.getBean(clazz) as AbstractCompositeCmd
			}
		val beansIntersect = reflectCommands.keys.all { it in _commandsProxyContainer.keys }
		val apiListIntersect = _commandsProxyContainer.keys.all { it in reflectCommands.keys }

		if (!(beansIntersect && apiListIntersect)) {
			val intersectKeys = _commandsProxyContainer.keys
				.union(reflectCommands.keys)
				.subtract(_commandsProxyContainer.keys.intersect(reflectCommands.keys))
			LOG.error("Contract between API and beans volated by at least one command. Cause by: {}", intersectKeys)
			exitProcess(-1)
		}
		reflectCommands.forEach { (commandName, beanDefinition) ->
			val commandProxyData = _commandsProxyContainer[commandName] ?: return
			commandProxyData.instance = beanDefinition
			LOG.info(
				"{}, {} ({}) - bean loaded via reflection",
				commandName, commandProxyData.aliases, beanDefinition.javaClass.name
			)
		}
	}

	val commandsProxyContainer: Map<String, CommandProxyData> get() = _commandsProxyContainer

	companion object {
		private val LOG = LoggerFactory.getLogger(CommandLoader::class.java)
		private const val SCANNING_BASE_PACKAGE = "pl.jwizard.core.api"
	}
}
