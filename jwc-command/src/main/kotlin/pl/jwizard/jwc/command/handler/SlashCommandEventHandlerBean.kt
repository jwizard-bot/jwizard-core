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

@JdaEventListenerBean
class SlashCommandEventHandlerBean(
	commandEventHandlerEnvironment: CommandEventHandlerEnvironmentBean,
) : CommandEventHandler<SlashCommandInteractionEvent>(commandEventHandlerEnvironment) {

	override val commandType = CommandType.SLASH

	override fun onSlashCommandInteraction(
		event: SlashCommandInteractionEvent,
	) = initPipelineAndPerformCommand(event, event.isFromGuild)

	override fun eventGuild(event: SlashCommandInteractionEvent) = event.guild

	override fun commandNameAndArguments(
		event: SlashCommandInteractionEvent,
		prefix: String,
	) = Pair(event.fullCommandName, event.options.map { it.asString })

	override fun createGuildCommandContext(
		event: SlashCommandInteractionEvent,
		command: String,
		properties: GuildMultipleProperties,
	) = SlashGuildCommandContext(command, event, properties)

	override fun createGlobalCommandContext(
		event: SlashCommandInteractionEvent,
		command: String,
	) = GlobalCommandContext(command, event)

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

	override fun deferAction(event: SlashCommandInteractionEvent, privateMessage: Boolean) {
		if (!event.isAcknowledged) {
			event.deferReply(privateMessage).queue()
		}
	}
}
