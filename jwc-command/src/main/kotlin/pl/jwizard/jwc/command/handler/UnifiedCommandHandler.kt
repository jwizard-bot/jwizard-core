/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
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

/**
 * Abstract class that handles various types of commands. It processes events and creates command contexts based on the
 * event type, whether it's a guild or global command.
 *
 * @param E The type of the event that this handler listens for, typically extending [Event].
 * @author Miłosz Gilga
 */
abstract class UnifiedCommandHandler<E : Event> : ListenerAdapter() {

	/**
	 * The type of command this handler processes.
	 */
	protected abstract val commandType: CommandType

	/**
	 * Retrieves the guild associated with the event.
	 *
	 * @param event The event being processed.
	 * @return The associated guild, or null if not applicable.
	 */
	protected abstract fun eventGuild(event: E): Guild?

	/**
	 * Checks if the event invocation details are forbidden based on guild properties.
	 *
	 * @param event The event being processed.
	 * @param properties The command properties for the guild.
	 * @return True if forbidden; otherwise, false.
	 */
	protected open fun forbiddenInvocationDetails(event: E, properties: GuildMultipleProperties) = false

	/**
	 * Extracts the command name and its arguments from the event.
	 *
	 * @param event The event being processed.
	 * @param prefix Command prefix. For slash command can always be empty string.
	 * @return A pair containing the command name and a list of arguments.
	 */
	protected abstract fun commandNameAndArguments(event: E, prefix: String): Pair<String, List<String>>

	/**
	 * Creates the command context from the event and the guild properties.
	 *
	 * @param event The event being processed.
	 * @param command Definition of the command on which the event was invoked.
	 * @param properties The command properties for the guild.
	 * @return The command context created from the event.
	 */
	protected abstract fun createGuildCommandContext(
		event: E,
		command: String,
		properties: GuildMultipleProperties,
	): GuildCommandContext

	/**
	 * Creates the global command context from the event.
	 *
	 * @param event The event being processed.
	 * @param command The name of the command invoked in the event.
	 * @return A [GlobalCommandContext], or `null` if global commands are not supported.
	 */
	protected open fun createGlobalCommandContext(event: E, command: String): GlobalCommandContext? = null

	/**
	 * Defers a message based on the event and the command response.
	 *
	 * @param event The event being processed.
	 * @param response The response generated from executing the command.
	 * @param privateMessage The value defined, if sending message should be private or public.
	 * @param suppressNotifications Determines if notifications from bot responses should be suppressed.
	 * @return A RestAction that sends the deferred message.
	 */
	protected abstract fun deferMessage(
		event: E,
		response: CommandResponse,
		privateMessage: Boolean,
		suppressNotifications: Boolean?,
	): RestAction<Message>

	/**
	 * Defers an action for the given event, which can be configured to be either public or private (ephemeral).
	 * This method can be overridden to customize the behavior based on the event type and context.
	 *
	 * @param event The event that triggered the action, typically containing the necessary context such as user, guild,
	 *        or message details.
	 * @param privateMessage A boolean indicating whether the response should be ephemeral (private) or public. If true,
	 *        the response will be sent as a private message (ephemeral); if false, it will be visible to everyone.
	 */
	protected open fun deferAction(event: E, privateMessage: Boolean) {}
}
