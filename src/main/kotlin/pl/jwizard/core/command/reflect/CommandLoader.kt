/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

import java.io.IOException
import kotlin.system.exitProcess
import pl.jwizard.core.bot.BotProperties
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.CommandModule
import pl.jwizard.core.exception.UtilException
import pl.jwizard.core.http.ApiUrl
import pl.jwizard.core.http.HttpClient
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.settings.GuildSettings
import com.fasterxml.jackson.core.type.TypeReference
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
	private val guildSettings: GuildSettings,
) : AbstractLoggingBean(CommandLoader::class) {

	final val commandsProxyContainer = mutableMapOf<String, AbstractCompositeCmd?>()
	private final var localizedCommands = mapOf<String, CommandsResDto>()

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
			localizedCommands = httpClient
				.mapResponseObject(response, object : TypeReference<Map<String, CommandsResDto>>() {})
				?: throw IOException()

			val langs = localizedCommands.keys
			val (categories, commands, modules) = localizedCommands.entries.first().value

			CommandModule.checkContractWithApi(modules)
			commands.forEach { commandsProxyContainer[it.key] = null }

			log.info("Fetched {} command categories from API (langs: {}): {}", categories.size, langs, url)
			log.info("Fetched {} commands from API (langs: {}): {}", commands.size, langs, url)
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
			.filter { (annotation, _) -> commandsProxyContainer.containsKey(annotation.id.commandName) }
			.associate { (annotation, clazz) ->
				annotation.id to applicationContext.getBean(clazz) as AbstractCompositeCmd
			}
		val beansIntersect = reflectCommands.keys.all { it.commandName in commandsProxyContainer.keys }
		val apiListIntersect = commandsProxyContainer.keys.all { it in reflectCommands.keys.map { c -> c.commandName } }

		if (!(beansIntersect && apiListIntersect)) {
			val intersectKeys = commandsProxyContainer.keys
				.union(reflectCommands.keys)
				.subtract(commandsProxyContainer.keys.intersect(reflectCommands.keys))
			log.error("Contract between API and beans volated by at least one command. Cause by: {}", intersectKeys)
			exitProcess(-1)
		}
		reflectCommands.forEach { (command, beanDefinition) ->
			commandsProxyContainer[command.commandName] = beanDefinition
			log.info("{} ({}) - bean loaded via reflection", command.commandName, beanDefinition.javaClass.name)
		}
	}

	fun getCommandsBaseLang(lang: String): CommandsResDto =
		localizedCommands[lang] ?: throw UtilException
			.UnexpectedException("Unsupported lang: $lang during getting commands data")

	fun getCommandBaseLang(command: String, lang: String): CommandDetailsDto? {
		val (_, commands) = getCommandsBaseLang(lang)
		return commands[command]
	}

	fun getModuleBaseLangInGuildId(guildId: String, module: CommandModule): String {
		val guildDetails = guildSettings.getGuildProperties(guildId)
		val lang = guildDetails.locale
		val selectedModule = localizedCommands[lang] ?: throw UtilException
			.UnexpectedException("Unsupported lang: $lang during getting modules data")
		return selectedModule.modules[module.moduleName] ?: "unknow"
	}

	companion object {
		private const val SCANNING_BASE_PACKAGE = "pl.jwizard.core.api"
	}
}
