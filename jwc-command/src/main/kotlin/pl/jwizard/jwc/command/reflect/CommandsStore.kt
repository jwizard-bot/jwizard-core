/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.reflect

import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger
import java.util.concurrent.ConcurrentHashMap

/**
 * A thread-safe store for commands, mapping each [Command] to a specific object of type [T].
 *
 * It is used to hold commands that are loaded dynamically via reflection and allows easy retrieval and modification.
 *
 * @param T The type of object that will be associated with each command.
 * @property commandsEnvironment The environment or context in which the commands are loaded.
 * @author Miłosz Gilga
 */
class CommandsStore<T : Any>(private val commandsEnvironment: String) : ConcurrentHashMap<Command, T>() {

	companion object {
		private val log = logger<CommandsStore<*>>()
	}

	/**
	 * A read-only collection of the commands that have been loaded into the store. It returns only the non-null keys of
	 * the map.
	 */
	val loadedCommands
		get() = keys.filterNotNull()

	/**
	 * Adds a new command to the store, associating it with a specific object of type [T]. This method uses reflection to
	 * load the command and log the event.
	 *
	 * @param command The [Command] to be added to the store.
	 * @param clazz The object of type [T] to associate with the given [command].
	 */
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
