/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.transport

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.components.ActionRow
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.handler.InteractionRemovalThread
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.spi.JdaInstance
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import java.util.concurrent.TimeUnit

/**
 * A component responsible for handling the transportation of command responses in a loosely coupled manner. It manages
 * the sending of messages to Discord channels and handles the removal of interaction components after a delay.
 *
 * @property jdaInstance The JDA instance used for interacting with Discord.
 * @property environmentBean The environment configuration for the bot.
 * @author Miłosz Gilga
 */
@Component
class LooselyTransportHandlerBean(
	private val jdaInstance: JdaInstance,
	private val environmentBean: EnvironmentBean,
) : DisposableBean {

	/**
	 * The maximum number of embed messages that can be sent in a single interaction response.
	 */
	private val maxEmbedMessagesBuffer = environmentBean.getProperty<Int>(BotProperty.JDA_INTERACTION_MESSAGE_MAX_EMBEDS)

	/**
	 * The maximum number of action rows allowed in an interaction response.
	 */
	private val maxActionRows = environmentBean.getProperty<Int>(BotProperty.JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_ROWS)

	/**
	 * The maximum number of components (like buttons) that can be included in a single action row.
	 */
	private val maxActionRowComponents =
		environmentBean.getProperty<Int>(BotProperty.JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_COMPONENTS_IN_ROW)

	/**
	 * The delay (in seconds) before remote interactions can be disabled after a command execution.
	 */
	private val remoteInteractionsDelay =
		environmentBean.getProperty<Long>(BotProperty.JDA_INTERACTION_MESSAGE_COMPONENT_DISABLE_DELAY_SEC)

	/**
	 * A thread responsible for removing interaction components from a message after a delay. It runs once per invocation
	 * and ensures that interactive elements (such as buttons) are disabled after the specified delay, preventing further
	 * user interaction.
	 */
	private val interactionRemovalThread = InteractionRemovalThread()

	/**
	 * Sends a command response to a specified text channel or as a private message to the user if necessary. It handles
	 * truncating the message components if they exceed the defined limits.
	 *
	 * @param textChannel The text channel where the response should be sent.
	 * @param response The command response to be sent, containing embed messages and action rows.
	 * @param privateUserId User id used to send private message. If it is `null`, message is public.
	 */
	fun sendViaChannelTransport(textChannel: TextChannel, response: CommandResponse, privateUserId: Long? = null) {
		val truncated = truncateComponents(response)

		val onSend: (Message) -> Unit = {
			if (response.disposeComponents) {
				startRemovalInteractionThread(it)
			}
			response.afterSendAction(it)
		}
		if (privateUserId == null) {
			textChannel.sendMessageEmbeds(response.embedMessages).addComponents(response.actionRows).queue(onSend)
			return
		}
		val user = jdaInstance.getUserById(privateUserId)
		user?.openPrivateChannel()?.queue {
			it.sendMessageEmbeds(truncated.embedMessages).addComponents(truncated.actionRows).queue(onSend)
		}
	}

	/**
	 * Truncates the components of the provided command response to ensure they do not exceed the maximum allowed limits.
	 *
	 * @param response The command response to be truncated.
	 * @return A new CommandResponse with truncated embed messages and action rows.
	 */
	fun truncateComponents(response: CommandResponse): CommandResponse {
		val truncatedEmbedMessages = response.embedMessages.take(maxEmbedMessagesBuffer)
		val truncatedActionRows = response.actionRows
			.map { row -> ActionRow.of(row.take(maxActionRowComponents)) }
			.take(maxActionRows)
		return response.copy(truncatedEmbedMessages, truncatedActionRows)
	}

	/**
	 * Starts a thread to remove interaction components (like buttons) from a message after a specified delay.
	 *
	 * This method schedules the removal of interaction components from the provided message after the
	 * [remoteInteractionsDelay] has passed.
	 *
	 * @param message The message from which interaction components will be removed.
	 */
	fun startRemovalInteractionThread(message: Message) {
		if (message.actionRows.isNotEmpty()) {
			interactionRemovalThread.startOnce(remoteInteractionsDelay, TimeUnit.SECONDS, message)
		}
	}

	/**
	 * Destroys the interaction removal thread when no longer needed.
	 * This method is called when the bean is destroyed.
	 */
	override fun destroy() = interactionRemovalThread.destroy()
}
