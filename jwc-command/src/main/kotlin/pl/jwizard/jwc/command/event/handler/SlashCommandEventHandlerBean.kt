/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.handler

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.requests.RestAction
import pl.jwizard.jwc.command.CommandsProxyStoreBean
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.event.CommandResponse
import pl.jwizard.jwc.command.event.CommandType
import pl.jwizard.jwc.command.event.context.SlashCommandContext
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.core.exception.ExceptionTrackerStore
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.EnvironmentBean

/**
 * Handles slash command interactions in a Discord server.
 *
 * @property commandDataSupplier Supplies command data.
 * @property moduleDataSupplier Supplies module data.
 * @property commandsProxyStoreBean Proxy for executing commands.
 * @property exceptionTrackerStore Tracks exceptions for reporting.
 * @property i18nBean Provides internationalization support.
 * @property environmentBean Accesses environment-specific properties.
 * @property jdaColorStoreBean Accesses to JDA defined colors for embed messages.
 * @author Miłosz Gilga
 * @see CommandEventHandler
 * @see SlashCommandInteractionEvent
 */
@JdaEventListenerBean
class SlashCommandEventHandlerBean(
	private val commandDataSupplier: CommandDataSupplier,
	private val moduleDataSupplier: ModuleDataSupplier,
	private val commandsProxyStoreBean: CommandsProxyStoreBean,
	private val exceptionTrackerStore: ExceptionTrackerStore,
	private val i18nBean: I18nBean,
	private val environmentBean: EnvironmentBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
) : CommandEventHandler<SlashCommandInteractionEvent>(
	commandDataSupplier,
	moduleDataSupplier,
	commandsProxyStoreBean,
	exceptionTrackerStore,
	i18nBean,
	environmentBean,
	jdaColorStoreBean,
) {

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
		event.deferReply().queue()
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
	 * @param properties The command properties for the guild.
	 * @return The command context.
	 */
	override fun createCommandContext(event: SlashCommandInteractionEvent, properties: GuildCommandProperties) =
		SlashCommandContext(event, properties)

	/**
	 * Sends a response message based on the command execution result.
	 *
	 * @param event The slash command interaction event.
	 * @param response The command response to send.
	 * @return The action to send the message.
	 */
	override fun deferMessage(event: SlashCommandInteractionEvent, response: CommandResponse): RestAction<Message> {
		val (embedMessages, actionRows) = response
		return if (event.hook.isExpired) {
			event.channel.sendMessageEmbeds(embedMessages).addComponents(actionRows)
		} else {
			event.hook.sendMessageEmbeds(embedMessages).setEphemeral(response.privateMessage).addComponents(actionRows)
		}
	}
}
