/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.handler

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.requests.RestAction
import pl.jwizard.jwc.command.CommandType
import pl.jwizard.jwc.command.context.GlobalCommandContext
import pl.jwizard.jwc.command.context.SlashGuildCommandContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties

/**
 * Handles slash command interactions in a Discord server.
 *
 * @property commandEventHandlerEnvironment Stored all beans for command event handler.
 * @author Miłosz Gilga
 */
@JdaEventListenerBean
class SlashCommandEventHandlerBean(
	private val commandEventHandlerEnvironment: CommandEventHandlerEnvironmentBean,
) : CommandEventHandler<SlashCommandInteractionEvent>(commandEventHandlerEnvironment) {

	override val commandType = CommandType.SLASH

	/**
	 * Handles a slash command interaction event by deferring the reply and initiating the command processing pipeline.
	 *
	 * @param event The slash command interaction event.
	 */
	override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) =
		initPipelineAndPerformCommand(event, event.isFromGuild)

	/**
	 * Retrieves the guild associated with the event.
	 *
	 * @param event The slash command interaction event.
	 * @return The guild from the event.
	 */
	override fun eventGuild(event: SlashCommandInteractionEvent) = event.guild

	/**
	 * Extracts the command name and its arguments from the event.
	 *
	 * @param event The slash command interaction event.
	 * @param prefix Command prefix. Can always be "/" for slash command invocations.
	 * @return A pair containing the command name and its arguments.
	 */
	override fun commandNameAndArguments(event: SlashCommandInteractionEvent, prefix: String) =
		Pair(event.fullCommandName, event.options.map { it.asString })

	/**
	 * Creates the command context specific to slash commands.
	 *
	 * @param event The slash command interaction event.
	 * @param command Definition of the command on which the event was invoked.
	 * @param properties The command properties for the guild.
	 * @return The command context.
	 */
	override fun createGuildCommandContext(
		event: SlashCommandInteractionEvent,
		command: String,
		properties: GuildMultipleProperties,
	) = SlashGuildCommandContext(event, command, properties)

	/**
	 * Creates a new instance of [GlobalCommandContext] based on the provided event and command.
	 *
	 * @param event The [SlashCommandInteractionEvent] that triggered the slash command.
	 * @param command The string representing the name of the command that was invoked.
	 * @return A new [GlobalCommandContext] instance that contains all the necessary information for processing the command.
	 */
	override fun createGlobalCommandContext(event: SlashCommandInteractionEvent, command: String) =
		GlobalCommandContext(event, command)

	/**
	 * Sends a response message based on the command execution result.
	 *
	 * @param event The slash command interaction event.
	 * @param response The command response to send.
	 * @param privateMessage The value defined, if sending message should be private or public.
	 * @param suppressNotifications Determines if notifications from bot responses should be suppressed.
	 * @return The action to send the message.
	 */
	override fun deferMessage(
		event: SlashCommandInteractionEvent,
		response: CommandResponse,
		privateMessage: Boolean,
		suppressNotifications: Boolean?,
	): RestAction<Message> {
		val embedMessages = response.embedMessages
		val message = if (event.hook.isExpired) {
			event.channel.sendMessageEmbeds(embedMessages)
		} else {
			event.hook.sendMessageEmbeds(embedMessages).setEphemeral(privateMessage)
		}
		message.addComponents(response.actionRows).setFiles(response.files)
		suppressNotifications?.let { message.setSuppressedNotifications(it) }
		response.pool?.let { message.setPoll(it) }
		return message
	}

	/**
	 * Overrides the deferAction method to handle deferred replies for slash command interactions. This method defers the
	 * reply to the slash command and allows specifying whether the response should be ephemeral (private) or public.
	 *
	 * @param event The slash command interaction event that triggered this action. It contains details about the command
	 *        and the user who invoked it.
	 * @param privateMessage A boolean indicating whether the response should be ephemeral (true) or visible to all users
	 *        (false).
	 */
	override fun deferAction(event: SlashCommandInteractionEvent, privateMessage: Boolean) {
		if (!event.isAcknowledged) {
			event.deferReply(privateMessage).queue()
		}
	}
}
