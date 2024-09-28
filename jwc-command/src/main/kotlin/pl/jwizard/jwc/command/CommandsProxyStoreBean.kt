/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.reflect.CommandDetails
import pl.jwizard.jwc.core.util.logger
import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap

/**
 * A Spring component that acts as a proxy store for managing command instances and metadata for the application.
 *
 * This bean is responsible for maintaining containers for command instances, modules, and command metadata,
 * facilitating easy lookup and management of commands.
 *
 * @author Miłosz Gilga
 */
@Component
class CommandsProxyStoreBean {

	companion object {
		private val log = logger<CommandsProxyStoreBean>()
	}

	/**
	 * A concurrent map that stores command instances, indexed by their name. The key is the command name, and the value
	 * is the [CommandBase] instance.
	 */
	val instancesContainer = ConcurrentHashMap<String, CommandBase>()

	/**
	 * A concurrent map that stores module information, mapping a unique module ID (as [BigInteger]) to its corresponding
	 * name.
	 */
	val modules = ConcurrentHashMap<BigInteger, String>()

	/**
	 * A map that maintains bidirectional integrity between command names and their metadata, using a
	 * [TwoWayIntegrityHashMap] for flexible key lookups.
	 */
	val commands = TwoWayIntegrityHashMap<String, CommandDetails>()

	/**
	 * Adds a command instance to the [instancesContainer] map.
	 *
	 * @param name The name of the command to be added.
	 * @param command The [CommandBase] instance of the command to be stored.
	 */
	fun addInstance(name: String, command: CommandBase) {
		instancesContainer[name] = command
		log.info("Command: \"{}\" ({}) appended via reflection.", name, command.javaClass.name)
	}
}
