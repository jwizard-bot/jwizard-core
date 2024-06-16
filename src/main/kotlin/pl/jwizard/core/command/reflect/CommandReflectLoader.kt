/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

import org.apache.commons.collections.CollectionUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.CommandModule
import pl.jwizard.core.command.CommandModule.Companion.getAllModuleNames
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.db.CommandsSupplier
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.BotUtils

@Component
class CommandReflectLoader(
	private val applicationContext: ApplicationContext,
	private val commandsSupplier: CommandsSupplier
) : AbstractLoggingBean(CommandReflectLoader::class) {

	private final var scanner = ClassPathScanningCandidateComponentProvider(false)

	private final var commandModules: Map<String, ModuleDetailsDto> = mutableMapOf()
	private final var botCommands: Map<String, CommandDetailsDto> = mutableMapOf()
	private final val commandsProxyContainer: MutableMap<String, AbstractCompositeCmd?> = mutableMapOf()

	init {
		scanner.addIncludeFilter(AnnotationTypeFilter(CommandListenerBean::class.java))
	}

	companion object {
		private const val SCANNING_BASE_PACKAGE = "pl.jwizard.core.api"
	}

	fun loadCommandsAndCheckDataIntegrity() {
		commandModules = commandsSupplier.fetchAllModules()
		val isModulesIntegrityEqual = CollectionUtils.isEqualCollection(
			getAllModuleNames(),
			commandModules.keys
		)
		if (!isModulesIntegrityEqual) {
			throw DataIntegrityViolationException("Contract between modules is not obtained!")
		}
		val commandArgs = commandsSupplier.fetchAllCommandArguments()
		if (!CommandArgument.checkIntegrity(commandArgs)) {
			throw DataIntegrityViolationException("Contract between command arguments in not obtained!")
		}
		botCommands = commandsSupplier.fetchAllCommands()
		botCommands.keys.forEach { commandsProxyContainer[it] = null }

		log.info("Fetched {} command modules from DB", commandModules.size)
		log.info("Fetched {} commands from DB", botCommands.size)
	}

	fun loadCommandsViaReflectionApi() {
		scanner.findCandidateComponents(SCANNING_BASE_PACKAGE)
			.map {
				val clazz = Class.forName(it.beanClassName)
				val annotation = clazz.getAnnotation(CommandListenerBean::class.java)
				annotation to clazz
			}
			.filter { (annotation, _) -> commandsProxyContainer.containsKey(annotation.id.commandName) }
			.forEach { (command, clazz) ->
				val commandName = command.id.commandName
				val beanDefinition = applicationContext.getBean(clazz) as AbstractCompositeCmd
				commandsProxyContainer[commandName] = beanDefinition
				log.info("{} ({}) - bean loaded via reflection", commandName, beanDefinition.javaClass.name)
			}
		val onlyWithNullableValues = commandsProxyContainer.entries
			.filter { it.value == null }
			.map { it.key }
		if (onlyWithNullableValues.isNotEmpty()) {
			throw DataIntegrityViolationException(
				"Contract between API and beans volated by at least one command! Cause by: $onlyWithNullableValues"
			)
		}
	}

	fun checkIfModuleIsEnabled(module: CommandModule, event: CompoundCommandEvent): ModuleStateDto {
		val selectedModule = commandModules[module.moduleName] ?: return ModuleStateDto()
		val isEnabled = commandsSupplier.checkIfModuleIsEnabled(module.moduleName, event.guildDbId)
		return ModuleStateDto(BotUtils.getLang(event.lang, selectedModule.name), isEnabled)
	}

	fun getCommandByNameOrAlias(commandNameOrAlias: String): CommandDetailsDto? = botCommands[commandNameOrAlias]
		?: botCommands.values.find { it.alias == commandNameOrAlias }

	fun getBotCommand(commandName: String): CommandDetailsDto? = botCommands[commandName]

	fun getCommandBean(commandName: String): AbstractCompositeCmd? = commandsProxyContainer[commandName]

	fun getCommandModules(): Map<String, ModuleDetailsDto> = commandModules

	fun getBotCommands(): Map<String, CommandDetailsDto> = botCommands
}
