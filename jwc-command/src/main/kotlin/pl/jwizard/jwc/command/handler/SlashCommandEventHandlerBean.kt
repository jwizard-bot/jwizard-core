/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.handler

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.requests.RestAction
import pl.jwizard.jwc.command.CommandType
import pl.jwizard.jwc.command.context.SlashCommandContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties

/**
 * Handles slash command interactions in a Discord server.
 *
 * @property commandEventHandlerEnvironmentBean Stored all beans for command event handler.
 * @author Miłosz Gilga
 * @see CommandEventHandler
 * @see SlashCommandInteractionEvent
 */
@JdaEventListenerBean
class SlashCommandEventHandlerBean(
	private val commandEventHandlerEnvironmentBean: CommandEventHandlerEnvironmentBean,
) : CommandEventHandler<SlashCommandInteractionEvent>(commandEventHandlerEnvironmentBean) {

	/**
	 * Specifies the command type as SLASH for this handler.
	 */
	override val commandType
		get() = CommandType.SLASH

	/**
	 * Handles a slash command interaction event by deferring the reply and initiating the command processing pipeline.
	 *
	 * @param event The slash command interaction event.
	 */
	override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
		initPipelineAndPerformCommand(event)
	}

	/**
	 * Checks if the command invocation is forbidden based on the event context.
	 *
	 * @param event The slash command interaction event.
	 * @return True if the invocation is forbidden; otherwise false.
	 */
	override fun forbiddenInvocationCondition(event: SlashCommandInteractionEvent) = !event.isFromGuild

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
	 * @return A pair containing the command name and its arguments.
	 */
	override fun commandNameAndArguments(event: SlashCommandInteractionEvent) =
		Pair(event.fullCommandName, event.options.map { it.asString })

	/**
	 * Creates the command context specific to slash commands.
	 *
	 * @param event The slash command interaction event.
	 * @param command Definition of the command on which the event was invoked.
	 * @param properties The command properties for the guild.
	 * @return The command context.
	 */
	override fun createCommandContext(
		event: SlashCommandInteractionEvent,
		command: String,
		properties: GuildMultipleProperties,
	) = SlashCommandContext(event, command, properties)

	/**
	 * Sends a response message based on the command execution result.
	 *
	 * @param event The slash command interaction event.
	 * @param response The command response to send.
	 * @param privateMessage The value defined, if sending message should be private or public.
	 * @return The action to send the message.
	 */
	override fun deferMessage(
		event: SlashCommandInteractionEvent,
		response: CommandResponse,
		privateMessage: Boolean,
	): RestAction<Message> {
		val embedMessages = response.embedMessages
		val message = if (event.hook.isExpired) {
			event.channel.sendMessageEmbeds(embedMessages)
		} else {
			event.hook.sendMessageEmbeds(embedMessages).setEphemeral(privateMessage)
		}
		message.addComponents(response.actionRows)
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
