package pl.jwizard.jwc.command

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.reflect.CommandsStore

@Component
internal class CommandsCache {
	val guildCommandInstances = CommandsStore<GuildCommandHandler>("guild")
	val globalCommandInstances = CommandsStore<GlobalCommandHandler>("global")
}
