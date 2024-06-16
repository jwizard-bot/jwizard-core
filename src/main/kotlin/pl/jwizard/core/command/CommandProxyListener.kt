/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.requests.RestAction
import org.springframework.stereotype.Component
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.arg.CommandArgumentData
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.reflect.CommandArgDto
import pl.jwizard.core.command.reflect.CommandDetailsDto
import pl.jwizard.core.command.reflect.CommandReflectLoader
import pl.jwizard.core.db.GuildSettingsSupplier
import pl.jwizard.core.exception.AbstractBotException
import pl.jwizard.core.exception.CommandException
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.Formatter
import java.util.*

@Component
class CommandProxyListener(
	private val commandReflectLoader: CommandReflectLoader,
	private val guildSettingsSupplier: GuildSettingsSupplier,
	private val botConfiguration: BotConfiguration,
) : AbstractLoggingBean(CommandProxyListener::class) {

	fun onRegularCommand(event: MessageReceivedEvent) {
		if (event.author.isBot || !event.isFromGuild) {
			return // skipping bot messages and non-guild message
		}
		val guildProps = guildSettingsSupplier.fetchGuildCommandProperties(event.guild.id)
			?: return // skipping for non finding persisted guild settings
		try {
			val messageContentWithPrefix = event.message.contentRaw
			if (!messageContentWithPrefix.startsWith(guildProps.prefix)) {
				return // skipping non-command message
			}
			val cmdWithArguments = messageContentWithPrefix.substring(1)
			val endPosition = messageContentWithPrefix.indexOf(' ')

			val commandName = if (endPosition > -1) {
				cmdWithArguments.substring(0, cmdWithArguments.indexOf(' '))
			} else {
				cmdWithArguments
			}
			val commandDetails = commandReflectLoader.getCommandByNameOrAlias(commandName)
				?: return // skipping, non existing command

			val compoundCommandEvent = CompoundCommandEvent(event, guildProps)
			if (!guildSettingsSupplier.checkIfCommandIsEnabled(guildProps.id, commandDetails.id, isSlashCommand = false)) {
				throw CommandException.CommandIsTurnedOffException(compoundCommandEvent, commandName)
			}
			val commandOptions: Queue<String> = LinkedList(cmdWithArguments
				.substring(commandName.length)
				.trim()
				.split("\\|")
				.filter { it.isNotEmpty() })
			if (commandOptions.size < commandDetails.args.filter { it.req }.size) {
				throwSyntaxException(compoundCommandEvent, commandName, commandDetails)
			}
			for (arg in commandDetails.args) {
				val optionMapping = commandOptions.poll()
				// check, if passed command with pre-defined options violate integrity
				checkIfArgumentOptionIsValid(compoundCommandEvent, arg, optionMapping)
				val argKey = CommandArgument.getInstation(arg.name)
				if (argKey == null || (optionMapping == null && arg.req)) {
					throwSyntaxException(compoundCommandEvent, commandName, commandDetails)
				}
				compoundCommandEvent.commandArgs[argKey!!] = CommandArgumentData(optionMapping, arg.type)
			}
			var interactiveMessage = InteractiveMessage()
			try {
				val command = commandReflectLoader.getCommandBean(commandDetails.name)
				interactiveMessage = command?.performCommand(compoundCommandEvent) ?: return
			} catch (ex: NumberFormatException) {
				throwSyntaxException(compoundCommandEvent, commandName, commandDetails)
			}
			val (messageEmbeds) = interactiveMessage
			if (messageEmbeds.isNotEmpty()) {
				val defferedSender = event.channel.sendMessageEmbeds(messageEmbeds)
				sendEmbeds(defferedSender, compoundCommandEvent)
			}
		} catch (ex: AbstractBotException) {
			val embedMessage = CustomEmbedBuilder(botConfiguration, guildProps.lang).buildErrorMessage(ex)
			event.channel.sendMessageEmbeds(embedMessage).queue()
		}
	}

	fun onSlashCommand(event: SlashCommandInteractionEvent) {
		if (!event.isFromGuild) {
			return // event not coming from guild, skipping
		}
		val commandName = event.fullCommandName
		val guildId = event.guild?.id ?: return
		val guildProps = guildSettingsSupplier.fetchGuildCommandProperties(guildId)
			?: return // skipping for non finding persisted guild settings
		try {
			event.deferReply().complete()

			val compoundCommandEvent = CompoundCommandEvent(event, guildProps)
			val commandDetails = commandReflectLoader.getBotCommand(commandName) ?: return
			if (!guildSettingsSupplier.checkIfCommandIsEnabled(guildProps.id, commandDetails.id, isSlashCommand = true)) {
				throw CommandException.CommandIsTurnedOffException(compoundCommandEvent, commandName)
			}
			val commandOptions: Queue<OptionMapping> = LinkedList(event.options)
			if (commandOptions.size < commandDetails.args.filter { it.req }.size) {
				throwSyntaxException(compoundCommandEvent, commandName, commandDetails)
			}
			for (arg in commandDetails.args) {
				val optionMapping = commandOptions.poll()
				val argKey = CommandArgument.getInstation(arg.name)
				checkIfArgumentOptionIsValid(compoundCommandEvent, arg, optionMapping.asString)
				compoundCommandEvent.commandArgs[argKey!!] = CommandArgumentData(optionMapping.asString, arg.type)
			}
			var interactiveMessage = InteractiveMessage()
			try {
				val command = commandReflectLoader.getCommandBean(commandName) ?: return
				interactiveMessage = command.performCommand(compoundCommandEvent)
			} catch (ex: NumberFormatException) {
				throwSyntaxException(compoundCommandEvent, commandName, commandDetails)
			}

			val (messageEmbeds, actionComponents) = interactiveMessage
			if (compoundCommandEvent.interactiveMessage.messageEmbeds.isNotEmpty()) {
				val defferedSender = if (!event.hook.isExpired) {
					val message = event.hook.sendMessageEmbeds(messageEmbeds)
					if (actionComponents.isNotEmpty()) {
						message.addActionRow(actionComponents)
					}
					message
				} else {
					event.channel.sendMessageEmbeds(messageEmbeds)
				}
				sendEmbeds(defferedSender, compoundCommandEvent)
			}
		} catch (ex: AbstractBotException) {
			val embedMessage = CustomEmbedBuilder(botConfiguration, guildProps.lang).buildErrorMessage(ex)
			val defferedSender = if (!event.hook.isExpired) {
				event.hook.sendMessageEmbeds(embedMessage)
			} else {
				event.channel.sendMessageEmbeds(embedMessage)
			}
			defferedSender.queue()
		}
	}

	private fun sendEmbeds(deferredMessages: RestAction<Message>, event: CompoundCommandEvent) {
		if (event.delay.isDefault()) { // without delay, default behaviour
			if (event.appendAfterEmbeds == null) { // without post-send action
				deferredMessages.queue()
			} else {  // append post-send embed
				deferredMessages.queue { event.appendAfterEmbeds?.let { it() } }
			}
		} else { // with delay
			val (duration, unit) = event.delay
			if (event.appendAfterEmbeds == null) { // without post-send action
				deferredMessages.queueAfter(duration, unit)
			} else { // append post-send embed
				val scheduledFuture = deferredMessages.queueAfter(duration, unit)
				if (scheduledFuture.isDone) {
					event.appendAfterEmbeds?.let { it() }
				}
			}
		}
	}

	private fun throwSyntaxException(
		event: CompoundCommandEvent,
		commandName: String,
		commandDetails: CommandDetailsDto,
	) {
		throw CommandException.MismatchCommandArgumentsException(
			event,
			commandName,
			syntax = Formatter.createCommandSyntax(
				commandName,
				commandDetails,
				legacyPrefix = event.legacyPrefix,
				lang = event.lang,
			),
		)
	}

	private fun checkIfArgumentOptionIsValid(event: CompoundCommandEvent, arg: CommandArgDto, option: String) {
		val flatOptions = arg.options.map { it.rawValue }
		if (arg.options.isNotEmpty() && !flatOptions.contains(option)) {
			throw CommandException.ViolatedCommandArgumentOptionsException(
				event,
				arg.name,
				option,
				flatOptions,
				Formatter.createArgumentOptionsSyntax(arg.options, event.lang)
			)
		}
	}
}
