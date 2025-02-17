package pl.jwizard.jwc.command.reflect

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.CommandsCache
import pl.jwizard.jwc.command.GlobalCommandHandler
import pl.jwizard.jwc.command.GuildCommandHandler
import pl.jwizard.jwc.core.jda.spi.CommandsLoader
import pl.jwizard.jwc.core.reflect.ClasspathScanner
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.util.logger
import kotlin.reflect.full.allSuperclasses

@Component
internal class CommandsLoaderImpl(
	private val ioCKtContextFactory: IoCKtContextFactory,
	private val commandsCache: CommandsCache,
) : CommandsLoader {
	companion object {
		private val log = logger<CommandsLoaderImpl>()
	}

	// scanner for reflect scanning commands in {basePackage}.jwc.api subpackage
	private val scanner = ClasspathScanner(JdaCommand::class, subpackage = "jwc.api")

	override fun loadClassesViaReflectionApi() {
		val global = commandsCache.globalCommandInstances
		val guild = commandsCache.guildCommandInstances
		for ((command, javaClazz) in scanner.findComponents()) {
			val commandBean = ioCKtContextFactory.getBean(javaClazz)
			val rootClazz = commandBean::class.allSuperclasses
			if (GlobalCommandHandler::class in rootClazz) {
				global[command.value] = commandBean as GlobalCommandHandler
			}
			if (GuildCommandHandler::class in rootClazz) {
				guild[command.value] = commandBean as GuildCommandHandler
			}
		}
		val guildDefinedCommands = Command.entries
		val globalDefinedCommands = guildDefinedCommands.filter { it.global }
		log.info(
			"Load: {} commands (defined in library: {}) including global: {} (defined in library: {}).",
			guild.loadedCommands.size,
			guildDefinedCommands.size,
			global.loadedCommands.size,
			globalDefinedCommands.size,
		)
		if (guild.loadedCommands.size != guildDefinedCommands.size) {
			val nonLoadedCommands = guildDefinedCommands - guild.loadedCommands.toSet()
			log.warn("Unable to load: {} guild commands: {}.", nonLoadedCommands.size, nonLoadedCommands)
		}
		if (global.loadedCommands.size != globalDefinedCommands.size) {
			val nonLoadedCommands = globalDefinedCommands - global.loadedCommands.toSet()
			log.warn("Unable to load: {} global commands: {}.", nonLoadedCommands.size, nonLoadedCommands)
		}
	}
}
