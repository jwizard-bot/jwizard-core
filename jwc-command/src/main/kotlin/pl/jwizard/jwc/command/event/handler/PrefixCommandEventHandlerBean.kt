/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.handler

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.RestAction
import pl.jwizard.jwc.command.CommandsProxyStoreBean
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.event.CommandType
import pl.jwizard.jwc.command.event.context.PrefixCommandContext
import pl.jwizard.jwc.command.event.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.core.exception.spi.ExceptionTrackerStore
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.EnvironmentBean

/**
 * Handles prefix commands received in messages.
 *
 * @property commandDataSupplier Supplies command data.
 * @property moduleDataSupplier Supplies module data.
 * @property commandsProxyStoreBean Proxy for command execution.
 * @property exceptionTrackerStore Tracks exceptions for reporting.
 * @property i18nBean Provides internationalization support.
 * @property environmentBean Accesses environment-specific properties.
 * @property jdaColorStoreBean Accesses to JDA defined colors for embed messages.
 * @property looselyTransportHandlerBean
 * @author Miłosz Gilga
 * @see CommandEventHandler
 * @see MessageReceivedEvent
 */
@JdaEventListenerBean
class PrefixCommandEventHandlerBean(
	private val commandDataSupplier: CommandDataSupplier,
	private val moduleDataSupplier: ModuleDataSupplier,
	private val commandsProxyStoreBean: CommandsProxyStoreBean,
	private val exceptionTrackerStore: ExceptionTrackerStore,
	private val i18nBean: I18nBean,
	private val environmentBean: EnvironmentBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
	private val looselyTransportHandlerBean: LooselyTransportHandlerBean,
) : CommandEventHandler<MessageReceivedEvent>(
	commandDataSupplier,
	moduleDataSupplier,
	commandsProxyStoreBean,
	exceptionTrackerStore,
	i18nBean,
	environmentBean,
	jdaColorStoreBean,
	looselyTransportHandlerBean,
) {

	companion object {
		/**
		 * Regular expression used to split command arguments.
		 */
		private const val COMMAND_ARGS_DELIMITER = "\\|"
	}

	/**
	 * Defines the command type as legacy for this handler.
	 */
	override val commandType
		get() = CommandType.LEGACY

	/**
	 * Handles the message received event by initializing the command processing pipeline.
	 *
	 * @param event The message received event.
	 */
	override fun onMessageReceived(event: MessageReceivedEvent) = initPipelineAndPerformCommand(event)

	/**
	 * Determines whether the command invocation is forbidden based on the event context.
	 *
	 * @param event The message received event.
	 * @return True if invocation is forbidden; otherwise false.
	 */
	override fun forbiddenInvocationCondition(event: MessageReceivedEvent) = event.author.isBot || !event.isFromGuild

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
	override fun forbiddenInvocationDetails(event: MessageReceivedEvent, properties: GuildCommandProperties): Boolean {
		val messageContentWithPrefix = event.message.contentRaw
		return !messageContentWithPrefix.startsWith(properties.prefix)
	}

	/**
	 * Parses the command name and arguments from the received message.
	 *
	 * @param event The message received event.
	 * @return A pair containing the command name and its arguments.
	 */
	override fun commandNameAndArguments(event: MessageReceivedEvent): Pair<String, List<String>> {
		val messageContentWithPrefix = event.message.contentRaw
		val commandWithArguments = messageContentWithPrefix.substring(1)
		val endPosition = messageContentWithPrefix.indexOf(' ')
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
	override fun createCommandContext(event: MessageReceivedEvent, command: String, properties: GuildCommandProperties) =
		PrefixCommandContext(event, command, properties)

	/**
	 * Sends a response message based on the command execution result.
	 *
	 * @param event The message received event.
	 * @param response The command response to send.
	 * @return The action to send the message.
	 */
	override fun deferMessage(event: MessageReceivedEvent, response: CommandResponse): RestAction<Message> {
		return event.channel.sendMessageEmbeds(response.embedMessages).addComponents(response.actionRows)
	}
}
