/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import pl.jwizard.jwc.command.reflect.CommandsStore
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * A singleton component that acts as a centralized cache for command instances. It maintains separate stores for
 * guild-specific commands and global commands.
 *
 * @author Miłosz Gilga
 */
@SingletonComponent
class CommandsCacheBean {

	/**
	 * A concurrent store that holds guild-specific command handlers, indexed by their command name. The key represents
	 * the name of the command, and the value is the corresponding [GuildCommandHandler] instance.
	 *
	 * This store is used to manage commands that are tied to a specific guild (server) context.
	 */
	val guildCommandInstances = CommandsStore<GuildCommandHandler>("guild")

	/**
	 * A concurrent store that holds global command handlers, indexed by their command name. The key represents the name
	 * of the command, and the value is the corresponding [GlobalCommandHandler] instance.
	 *
	 * This store is used to manage commands that can operate outside the guild context, such as global or DM commands.
	 */
	val globalCommandInstances = CommandsStore<GlobalCommandHandler>("global")
}
