/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.handler

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.RestAction
import pl.jwizard.jwc.command.CommandType
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.context.PrefixGuildCommandContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty

/**
 * Handles prefix commands received in messages.
 *
 * @property commandEventHandlerEnvironment Stored all beans for command event handler.
 * @author Miłosz Gilga
 */
@JdaEventListenerBean
class PrefixCommandEventHandlerBean(
	private val commandEventHandlerEnvironment: CommandEventHandlerEnvironmentBean,
) : CommandEventHandler<MessageReceivedEvent>(commandEventHandlerEnvironment) {

	companion object {
		/**
		 * Regular expression used to split command arguments.
		 */
		private const val COMMAND_ARGS_DELIMITER = "\\|"
	}

	override val commandType = CommandType.PREFIX

	/**
	 * Handles the message received event by initializing the command processing pipeline.
	 *
	 * @param event The message received event.
	 */
	override fun onMessageReceived(event: MessageReceivedEvent) {
		if (!event.author.isBot && event.isFromGuild) {
			initPipelineAndPerformCommand(event, fromGuild = true)
		}
	}

	/**
	 * Retrieves the guild associated with the event.
	 *
	 * @param event The message received event.
	 * @return The guild from the event.
	 */
	override fun eventGuild(event: MessageReceivedEvent) = event.guild

	/**
	 * Checks if the command invocation details are forbidden based on properties.
	 *
	 * @param event The message received event.
	 * @param properties The command properties for the guild.
	 * @return True if invocation details are forbidden; otherwise false.
	 */
	override fun forbiddenInvocationDetails(event: MessageReceivedEvent, properties: GuildMultipleProperties): Boolean {
		val messageContentWithPrefix = event.message.contentRaw
		val prefix = properties.getProperty<String>(GuildProperty.LEGACY_PREFIX)
		val instancePrefix = environment.getProperty<String>(BotProperty.JDA_INSTANCE_PREFIX)
		return !messageContentWithPrefix.startsWith(prefix + instancePrefix)
	}

	/**
	 * Parses the command name and arguments from the received message.
	 *
	 * @param event The message received event.
	 * @param prefix Command prefix received from guild settings.
	 * @return A pair containing the command name and its arguments.
	 */
	override fun commandNameAndArguments(event: MessageReceivedEvent, prefix: String): Pair<String, List<String>> {
		val messageContentWithPrefix = event.message.contentRaw

		val instancePrefix = environment.getProperty<String>(BotProperty.JDA_INSTANCE_PREFIX)
		val lengthToOmit = (prefix + instancePrefix).length

		val commandWithArguments = messageContentWithPrefix.substring(lengthToOmit + 1)
		val endPosition = commandWithArguments.indexOf(' ')
		val commandNameOrAlias = if (endPosition > -1) {
			commandWithArguments.substring(0, commandWithArguments.indexOf(' '))
		} else {
			commandWithArguments
		}
		val commandOptions = commandWithArguments
			.substring(commandNameOrAlias.length)
			.trim()
			.split(COMMAND_ARGS_DELIMITER)
			.filter(String::isNotEmpty)
		return Pair(commandNameOrAlias, commandOptions)
	}

	/**
	 * Creates the command context specific to prefix commands.
	 *
	 * @param event The message received event.
	 * @param command Definition of the command on which the event was invoked.
	 * @param properties The command properties for the guild.
	 * @return The command context.
	 */
	override fun createGuildCommandContext(
		event: MessageReceivedEvent,
		command: String,
		properties: GuildMultipleProperties
	): GuildCommandContext {
		val instancePrefix = environment.getProperty<String>(BotProperty.JDA_INSTANCE_PREFIX)
		return PrefixGuildCommandContext(event, instancePrefix, command, properties)
	}

	/**
	 * Sends a response message based on the command execution result.
	 *
	 * @param event The message received event.
	 * @param response The command response to send.
	 * @param privateMessage The value defined, if sending message should be private or public.
	 * @param suppressNotifications Determines if notifications from bot responses should be suppressed.
	 * @return The action to send the message.
	 */
	override fun deferMessage(
		event: MessageReceivedEvent,
		response: CommandResponse,
		privateMessage: Boolean,
		suppressNotifications: Boolean?,
	): RestAction<Message> {
		val message = event.channel
			.sendMessageEmbeds(response.embedMessages)
			.addComponents(response.actionRows)
			.setFiles(response.files)
		suppressNotifications?.let { message.setSuppressedNotifications(it) }
		response.pool?.let { message.setPoll(it) }
		return message
	}
}
