/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger
import java.util.concurrent.ConcurrentHashMap

/**
 * An IoC component that manages a cache of command instances. This class provides functionality to store and
 * retrieve command instances in a thread-safe manner.
 *
 * @author Miłosz Gilga
 */
@SingletonComponent
class CommandsCacheBean {

	companion object {
		private val log = logger<CommandsCacheBean>()
	}

	/**
	 * A concurrent map that stores command instances, indexed by their name. The key is the command name, and the value
	 * is the [CommandBase] instance.
	 */
	val instancesContainer = ConcurrentHashMap<Command, CommandBase>()

	/**
	 * Adds a command instance to the [instancesContainer] map.
	 *
	 * @param name The name of the command to be added.
	 * @param command The [CommandBase] instance of the command to be stored.
	 */
	fun addInstance(name: Command, command: CommandBase) {
		instancesContainer[name] = command
		log.info("Command: \"{}\" ({}) appended via reflection.", name, command.javaClass.name)
	}
}
