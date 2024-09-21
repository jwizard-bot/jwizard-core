/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.reflect

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.CommandsProxyStoreBean
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.refer.CommandArgument
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.core.DiscordBotAppRunner.BASE_PACKAGE
import pl.jwizard.jwc.core.SpringKtContextFactory
import pl.jwizard.jwc.core.integrity.ReferentialIntegrityChecker
import pl.jwizard.jwc.core.jda.spi.CommandsLoader

/**
 * A bean responsible for loading command metadata and classes within the Discord bot framework.
 *
 * This class interacts with data suppliers to fetch commands, command arguments, and modules, ensuring integrity before
 * storing them in the command proxy.
 *
 * @property moduleDataSupplier The supplier for module data.
 * @property commandDataSupplier The supplier for command data.
 * @property springKtContextFactory The context factory for retrieving Spring beans.
 * @property commandsProxyStoreBean The store for command proxies.
 * @author Miłosz Gilga
 */
@Component
class CommandsLoaderBean(
	private val moduleDataSupplier: ModuleDataSupplier,
	private val commandDataSupplier: CommandDataSupplier,
	private val springKtContextFactory: SpringKtContextFactory,
	private val commandsProxyStoreBean: CommandsProxyStoreBean,
) : CommandsLoader {

	companion object {
		private val log = LoggerFactory.getLogger(CommandsLoaderBean::class.java)

		/**
		 * Base package used for scanning command classes.
		 */
		private const val SCANNING_BASE_PACKAGE = "$BASE_PACKAGE.jwc.api"
	}

	/**
	 * Scanner used for detecting classes annotated with [JdaCommand].
	 */
	private val scanner = ClassPathScanningCandidateComponentProvider(false)

	init {
		scanner.addIncludeFilter(AnnotationTypeFilter(JdaCommand::class.java))
	}

	/**
	 * Loads command metadata from the data source and verifies the integrity of modules, arguments, and commands. This
	 * method populates the command proxy store with the fetched data.
	 */
	override fun loadMetadata() {
		val suppliedModules = moduleDataSupplier.getModules()
		log.info("Fetch: {} modules from datasource.", suppliedModules.size)
		commandsProxyStoreBean.modules.putAll(suppliedModules)

		val suppliedCommandArguments = commandDataSupplier.getCommandArgumentKeys()
		log.info("Fetch: {} command arguments from datasource.", suppliedCommandArguments.size)
		ReferentialIntegrityChecker.checkIntegrity<CommandArgument>(this::class, suppliedCommandArguments)

		val suppliedCommands = commandDataSupplier.getCommands()
		log.info("Fetch: {} commands from datasource.", suppliedCommands.size)
		ReferentialIntegrityChecker.checkIntegrity<Command>(this::class, suppliedCommands.keys)
		commandsProxyStoreBean.commands.putAll(suppliedCommands)
	}

	/**
	 * Loads command classes using reflection. This method scans the classpath for classes annotated with [JdaCommand]
	 * and registers them in the command proxy store.
	 */
	override fun loadClassesViaReflectionApi() {
		scanner.findCandidateComponents(SCANNING_BASE_PACKAGE)
			.map {
				val clazz = Class.forName(it.beanClassName)
				clazz.getAnnotation(JdaCommand::class.java) to clazz
			}
			.forEach { (command, clazz) ->
				commandsProxyStoreBean.addInstance(command.id.propName, springKtContextFactory.getBean(clazz) as CommandBase)
			}
		val loadedCommands = commandsProxyStoreBean.instancesContainer.keys
		val commands = commandsProxyStoreBean.commands
		log.info("Load: {} commands ({} persisted in DB) classes.", loadedCommands.size, commands.size)
		if (loadedCommands.size != commands.size) {
			val nonLoadedCommands = commands.keys - loadedCommands
			log.warn("Unable to load: {} commands: {}.", nonLoadedCommands.size, nonLoadedCommands)
		}
	}
}
