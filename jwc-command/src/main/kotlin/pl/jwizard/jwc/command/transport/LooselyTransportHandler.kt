package pl.jwizard.jwc.command.transport

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.components.ActionRow
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.JdaShardManager
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwl.property.BaseEnvironment
import java.util.concurrent.TimeUnit

@Component
class LooselyTransportHandler(
	private val jdaShardManager: JdaShardManager,
	environment: BaseEnvironment,
) : DisposableBean {
	private val maxEmbedMessagesBuffer = environment
		.getProperty<Int>(BotProperty.JDA_INTERACTION_MESSAGE_MAX_EMBEDS)

	private val maxActionRows = environment
		.getProperty<Int>(BotProperty.JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_ROWS)

	private val maxActionRowComponents = environment
		.getProperty<Int>(BotProperty.JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_COMPONENTS_IN_ROW)

	private val remoteInteractionsDelay = environment
		.getProperty<Long>(BotProperty.JDA_INTERACTION_MESSAGE_COMPONENT_DISABLE_DELAY_SEC)

	// thread responsible for removing interaction components from a message after a delay
	// runs once per invocation and ensures that interactive elements (such as buttons) are disabled
	// after the specified delay
	private val interactionRemovalThread = InteractionRemovalThread()

	fun sendViaChannelTransport(
		textChannel: TextChannel,
		response: CommandResponse,
		notificationsSuppressed: Boolean,
		privateUserId: Long? = null,
	) {
		val truncated = truncateComponents(response)
		val onSend: (Message) -> Unit = {
			if (response.disposeComponents) {
				startRemovalInteractionThread(it)
			}
			response.afterSendAction(it)
		}
		if (privateUserId == null) {
			textChannel.sendMessageEmbeds(response.embedMessages)
				.addComponents(response.actionRows)
				.setSuppressedNotifications(notificationsSuppressed)
				.queue(onSend)
			return
		}
		val user = jdaShardManager.getUserById(privateUserId)
		user?.openPrivateChannel()?.queue {
			it.sendMessageEmbeds(truncated.embedMessages)
				.addComponents(truncated.actionRows)
				.setSuppressedNotifications(notificationsSuppressed)
				.queue(onSend)
		}
	}

	fun truncateComponents(response: CommandResponse): CommandResponse {
		val truncatedEmbedMessages = response.embedMessages.take(maxEmbedMessagesBuffer)
		val truncatedActionRows = response.actionRows
			.map { row -> ActionRow.of(row.take(maxActionRowComponents)) }
			.take(maxActionRows)
		return response.copy(truncatedEmbedMessages, truncatedActionRows)
	}

	fun startRemovalInteractionThread(message: Message) {
		if (message.actionRows.isNotEmpty()) {
			interactionRemovalThread.startOnce(remoteInteractionsDelay, TimeUnit.SECONDS, message)
		}
	}

	override fun destroy() = interactionRemovalThread.destroy()
}
