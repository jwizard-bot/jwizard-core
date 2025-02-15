package pl.jwizard.jwc.command.handler

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.RestAction
import pl.jwizard.jwc.command.CommandType
import pl.jwizard.jwc.command.context.GlobalCommandContext
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties

abstract class UnifiedCommandHandler<E : Event> : ListenerAdapter() {
	protected abstract val commandType: CommandType

	protected abstract fun eventGuild(event: E): Guild?

	protected open fun forbiddenInvocationDetails(
		event: E,
		properties: GuildMultipleProperties,
	) = false

	protected abstract fun commandNameAndArguments(
		event: E,
		prefix: String,
	): Pair<String, List<String>>

	protected abstract fun createGuildCommandContext(
		event: E,
		command: String,
		properties: GuildMultipleProperties,
	): GuildCommandContext

	protected open fun createGlobalCommandContext(
		event: E,
		command: String,
	): GlobalCommandContext? = null

	protected abstract fun deferMessage(
		event: E,
		response: CommandResponse,
		privateMessage: Boolean,
		suppressNotifications: Boolean?,
	): RestAction<Message>

	protected open fun deferAction(event: E, privateMessage: Boolean) {}
}
