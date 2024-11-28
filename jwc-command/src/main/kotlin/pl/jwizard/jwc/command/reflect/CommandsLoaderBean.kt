/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.reflect

import pl.jwizard.jwc.command.CommandHandler
import pl.jwizard.jwc.command.CommandsCacheBean
import pl.jwizard.jwc.core.jda.spi.CommandsLoader
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.ioc.reflect.ClasspathScanner
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger

/**
 * The CommandsLoaderBean class is responsible for loading command classes annotated with [JdaCommand] using reflection.
 * It scans the classpath for these command classes and registers them into the command cache for later use.
 *
 * @property ioCKtContextFactory Provides access to the IoC context for retrieving beans.
 * @property commandsCache The cache for commands.
 * @author Miłosz Gilga
 */
@SingletonComponent
class CommandsLoaderBean(
	private val ioCKtContextFactory: IoCKtContextFactory,
	private val commandsCache: CommandsCacheBean,
) : CommandsLoader {

	companion object {
		private val log = logger<CommandsLoaderBean>()

		/**
		 * Subpackage used for scanning command classes.
		 */
		private const val SCANNING_SUBPACKAGE = "jwc.api"
	}

	/**
	 * Scanner used for detecting classes annotated with [JdaCommand].
	 */
	private val scanner = ClasspathScanner(JdaCommand::class, SCANNING_SUBPACKAGE)

	/**
	 * Loads command classes using reflection. This method scans the classpath for classes annotated with [JdaCommand]
	 * and registers them in the command proxy store.
	 */
	override fun loadClassesViaReflectionApi() {
		scanner.findComponents().forEach { (command, clazz) ->
			commandsCache.addInstance(command.value, ioCKtContextFactory.getBean(clazz) as CommandHandler)
		}
		val loadedCommands = commandsCache.instancesContainer.keys.filterNotNull()
		val allCommands = Command.entries.size
		log.info("Load: {} commands ({} defined in library) classes.", loadedCommands.size, allCommands)
		if (loadedCommands.size != allCommands) {
			val nonLoadedCommands = Command.entries.map(Command::textKey) - loadedCommands.toSet()
			log.warn("Unable to load: {} commands: {}.", nonLoadedCommands.size, nonLoadedCommands)
		}
	}
}
