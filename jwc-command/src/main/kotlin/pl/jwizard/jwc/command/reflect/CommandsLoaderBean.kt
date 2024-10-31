/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.reflect

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.CommandsCacheBean
import pl.jwizard.jwc.core.jda.spi.CommandsLoader
import pl.jwizard.jwl.AppRunner
import pl.jwizard.jwl.SpringKtContextFactory
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * TODO
 *
 * @property springKtContextFactory The context factory for retrieving Spring beans.
 * @property commandsCacheBean The cache for commands.
 * @author Miłosz Gilga
 */
@Component
class CommandsLoaderBean(
	private val springKtContextFactory: SpringKtContextFactory,
	private val commandsCacheBean: CommandsCacheBean,
) : CommandsLoader {

	companion object {
		private val log = logger<CommandsLoaderBean>()

		/**
		 * Base package used for scanning command classes.
		 */
		private const val SCANNING_BASE_PACKAGE = "${AppRunner.BASE_PACKAGE}.jwc.api"
	}

	/**
	 * Scanner used for detecting classes annotated with [JdaCommand].
	 */
	private val scanner = ClassPathScanningCandidateComponentProvider(false)

	init {
		scanner.addIncludeFilter(AnnotationTypeFilter(JdaCommand::class.java))
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
				command.value.textId?.let {
					commandsCacheBean.addInstance(it, springKtContextFactory.getBean(clazz) as CommandBase)
				}
			}
		val loadedCommands = commandsCacheBean.instancesContainer.keys.filterNotNull()
		val allCommands = Command.entries.size
		log.info("Load: {} commands ({} defined in library) classes.", loadedCommands.size, allCommands)
		if (loadedCommands.size != allCommands) {
			val nonLoadedCommands = Command.entries.map(Command::textId) - loadedCommands.toSet()
			log.warn("Unable to load: {} commands: {}.", nonLoadedCommands.size, nonLoadedCommands)
		}
	}
}
