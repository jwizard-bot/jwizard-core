package pl.jwizard.jwc.command.reflect

import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger
import java.util.concurrent.ConcurrentHashMap

class CommandsStore<T : Any>(
	private val commandsEnvironment: String,
) : ConcurrentHashMap<Command, T>() {

	companion object {
		private val log = logger<CommandsStore<*>>()
	}

	val loadedCommands
		get() = keys.filterNotNull()

	val loadedSlashCommands
		get() = loadedCommands.filter { it.slashAvailable }

	operator fun set(command: Command, clazz: T) {
		super.put(command, clazz)
		log.info(
			"({}) Command: \"{}\" ({}) appended via reflection.",
			commandsEnvironment,
			command,
			command.javaClass.name
		)
	}
}
