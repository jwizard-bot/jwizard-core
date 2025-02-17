package pl.jwizard.jwc.command.handler

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.RestAction
import pl.jwizard.jwc.command.CommandType
import pl.jwizard.jwc.command.context.PrefixGuildCommandContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.event.JdaEventListener
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty

@JdaEventListener
internal class PrefixCommandEventHandler(
	commandEventHandlerEnvironment: CommandEventHandlerEnvironment,
) : CommandEventHandler<MessageReceivedEvent>(commandEventHandlerEnvironment) {
	companion object {
		// Regular expression to delimit command arguments
		private const val COMMAND_ARGS_DELIMITER = "\\|"
	}

	override val commandType = CommandType.PREFIX

	override fun onMessageReceived(event: MessageReceivedEvent) {
		if (!event.author.isBot && event.isFromGuild) {
			initPipelineAndPerformCommand(event, fromGuild = true)
		}
	}

	override fun eventGuild(event: MessageReceivedEvent) = event.guild

	override fun forbiddenInvocationDetails(
		event: MessageReceivedEvent,
		properties: GuildMultipleProperties,
	): Boolean {
		val messageContentWithPrefix = event.message.contentRaw
		val prefix = properties.getProperty<String>(GuildProperty.LEGACY_PREFIX)
		val instancePrefix = environment.getProperty<String>(BotProperty.JDA_INSTANCE_PREFIX)
		return !messageContentWithPrefix.startsWith(prefix + instancePrefix)
	}

	override fun commandNameAndArguments(
		event: MessageReceivedEvent,
		prefix: String,
	): Pair<String, List<String>> {
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

	override fun createGuildCommandContext(
		event: MessageReceivedEvent,
		command: String,
		properties: GuildMultipleProperties,
	) = PrefixGuildCommandContext(
		command,
		event,
		environment.getProperty<String>(BotProperty.JDA_INSTANCE_PREFIX),
		properties
	)

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
