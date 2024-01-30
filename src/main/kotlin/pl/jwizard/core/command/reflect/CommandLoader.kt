/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

import java.io.IOException
import kotlin.system.exitProcess
import pl.jwizard.core.bot.BotProperties
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.http.ApiUrl
import pl.jwizard.core.http.HttpClient
import pl.jwizard.core.utils.AbstractLoggingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import okhttp3.Request

@Component
class CommandLoader(
	private val applicationContext: ApplicationContext,
	private val botProperties: BotProperties,
	private val httpClient: HttpClient,
) : AbstractLoggingBean(CommandLoader::class) {

	final val commandsProxyContainer = mutableMapOf<String, CommandProxyData>()
	private final val categoriesProxyContainer = mutableMapOf<String, String>()
	private var scanner = ClassPathScanningCandidateComponentProvider(false)

	init {
		scanner.addIncludeFilter(AnnotationTypeFilter(CommandListenerBean::class.java))
	}

	fun fetchCommandsFromApi() {
		val url = ApiUrl.ALL_COMMANDS_WITH_CATEGORIES.getUrl(botProperties)
		val request = Request.Builder()
			.url(url)
			.build()
		try {
			val response = httpClient.makeSecureBlockCall(request)
			if (response.code != 200) {
				throw IOException(response.body?.string())
			}
			val (categories, commands) = httpClient
				.mapResponseObject(response, CommandsResDto::class)
				?: throw IOException()
			categories.forEach { (key, value) -> categoriesProxyContainer[key] = value }
			commands.forEach { (key, value) -> commandsProxyContainer[key] = CommandProxyData(value) }

			log.info("Fetched {} command categories from API: {}", categories.size, url)
			log.info("Fetched {} commands from API: {}", commands.size, url)
		} catch (ex: IOException) {
			log.error("Unable to load command categories and commands. Cause: {}", ex.message)
			exitProcess(-1)
		}
	}

	fun reflectAndLoadCommands() {
		val reflectCommands = scanner
			.findCandidateComponents(SCANNING_BASE_PACKAGE)
			.map {
				val clazz = Class.forName(it.beanClassName)
				val annotation = clazz.getAnnotation(CommandListenerBean::class.java)
				annotation to clazz
			}
			.filter { (annotation, _) -> commandsProxyContainer.containsKey(annotation.id) }
			.associate { (annotation, clazz) ->
				annotation.id to applicationContext.getBean(clazz) as AbstractCompositeCmd
			}
		val beansIntersect = reflectCommands.keys.all { it in commandsProxyContainer.keys }
		val apiListIntersect = commandsProxyContainer.keys.all { it in reflectCommands.keys }

		if (!(beansIntersect && apiListIntersect)) {
			val intersectKeys = commandsProxyContainer.keys
				.union(reflectCommands.keys)
				.subtract(commandsProxyContainer.keys.intersect(reflectCommands.keys))
			log.error("Contract between API and beans volated by at least one command. Cause by: {}", intersectKeys)
			exitProcess(-1)
		}
		reflectCommands.forEach { (commandName, beanDefinition) ->
			val commandProxyData = commandsProxyContainer[commandName] ?: return
			commandProxyData.instance = beanDefinition
			log.info(
				"{}, {} ({}) - bean loaded via reflection",
				commandName, commandProxyData.aliases, beanDefinition.javaClass.name
			)
		}
	}

	companion object {
		private const val SCANNING_BASE_PACKAGE = "pl.jwizard.core.api"
	}
}
