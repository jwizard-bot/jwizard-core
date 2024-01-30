/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import pl.jwizard.core.command.reflect.CommandLoader
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

@Component
class CommandProxyListener(
	private val commandLoader: CommandLoader
) : ListenerAdapter() {

	override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
		if (event.author.isBot) {
			return // skipping bot messages
		}
		val messageContentWithPrefix = event.message.contentRaw
		if (!messageContentWithPrefix.startsWith("$")) {
			return // skipping non-command message
		}
		val commandName = messageContentWithPrefix.substring(1)
		val command = commandLoader.commandsProxyContainer[commandName] ?: return

		val compoundCommandEvent = CompoundCommandEvent(event)
		val instance = command.instance ?: return
		val messages = instance.performCommand(compoundCommandEvent)

		event.channel.sendMessageEmbeds(messages).queue()
	}

	override fun onSlashCommand(event: SlashCommandEvent) {
		val command = commandLoader.commandsProxyContainer[event.commandString.substring(1)] ?: return
		event.deferReply().queue()

		val compoundCommandEvent = CompoundCommandEvent(event)
		val instance = command.instance ?: return
		val messages = instance.performCommand(compoundCommandEvent)

		if (!event.hook.isExpired) {
			val (duration, unit) = compoundCommandEvent.delay
			event.hook
				.sendMessageEmbeds(compoundCommandEvent.messageEmbeds)
				.queueAfter(duration, unit)
		} else {
			event.channel.sendMessageEmbeds(messages).queue()
		}
	}
}
