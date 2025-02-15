package pl.jwizard.jwc.command

import pl.jwizard.jwc.command.reflect.CommandsStore
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

@SingletonComponent
internal class CommandsCacheBean {
	val guildCommandInstances = CommandsStore<GuildCommandHandler>("guild")
	val globalCommandInstances = CommandsStore<GlobalCommandHandler>("global")
}
