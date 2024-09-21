/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.handler

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.requests.RestAction
import org.springframework.beans.factory.DisposableBean
import pl.jwizard.jwc.command.CommandsProxyStoreBean
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.event.CommandResponse
import pl.jwizard.jwc.command.event.CommandType
import pl.jwizard.jwc.command.event.arg.CommandArgumentParsingData
import pl.jwizard.jwc.command.event.arg.CommandArgumentType
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.event.exception.*
import pl.jwizard.jwc.command.refer.CommandArgument
import pl.jwizard.jwc.command.reflect.CommandArgumentDetails
import pl.jwizard.jwc.command.reflect.CommandDetails
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.core.exception.CommandPipelineException
import pl.jwizard.jwc.core.exception.ExceptionTrackerStore
import pl.jwizard.jwc.core.exception.UnexpectedException
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.source.I18nDynamicMod
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Abstract base class for handling command events in the Discord bot.
 *
 * This class is responsible for processing incoming events, executing commands, managing exceptions, and responding
 * to the user appropriately.
 *
 * @property commandDataSupplier The supplier for command data.
 * @property moduleDataSupplier The supplier for module data.
 * @property commandsProxyStoreBean The bean for storing command proxies.
 * @property exceptionTrackerStore The bean for tracking exceptions.
 * @property i18nBean The bean for internationalization.
 * @property environmentBean The bean for environment properties.
 * @property jdaColorStoreBean Accesses to JDA defined colors for embed messages.
 * @author Miłosz Gilga
 */
abstract class CommandEventHandler<E : Event>(
	private val commandDataSupplier: CommandDataSupplier,
	private val moduleDataSupplier: ModuleDataSupplier,
	private val commandsProxyStoreBean: CommandsProxyStoreBean,
	private val exceptionTrackerStore: ExceptionTrackerStore,
	private val i18nBean: I18nBean,
	private val environmentBean: EnvironmentBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
) : ListenerAdapter(), DisposableBean {

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
	 * Thread responsible for managing the removal of interaction components from messages.
	 */
	private val interactionRemovalThread = InteractionRemovalThread()

	/**
	 * Initializes the command pipeline and performs the command based on the event. This method handles the main logic
	 * of checking command conditions, parsing arguments, and executing the command.
	 *
	 * @param event The event that triggered this handler.
	 */
	protected fun initPipelineAndPerformCommand(event: E) {
		var directEphemeralUser: User? = null
		var context: CommandContext? = null
		var commandResponse: CommandResponse
		try {
			try {
				if (forbiddenInvocationCondition(event)) {
					throw CommandInvocationException("forbidden invocation condition")
				}
				val guild = eventGuild(event) ?: throw CommandInvocationException("guild is null")
				val properties = commandDataSupplier.getCommandPropertiesFromGuild(guild.id)
					?: throw CommandInvocationException("properties not exists")

				if (forbiddenInvocationDetails(event, properties)) {
					throw CommandInvocationException("forbidden invocation details")
				}
				val (commandNameOrAlias, commandArguments) = commandNameAndArguments(event)
				context = createCommandContext(event, properties)

				val commandDetails = commandsProxyStoreBean.commands[commandNameOrAlias]
					?: throw CommandInvocationException("command by command name could not be found", context)

				val (moduleId, isEnabled) = moduleDataSupplier.isEnabled(commandDetails.name, properties.guildDbId)
					?: throw CommandInvocationException("module by command name could not be found", context)
				if (!isEnabled) {
					val moduleName = i18nBean.tRaw(I18nDynamicMod.MODULES_MOD, arrayOf(moduleId), context.guildLanguage)
					throw ModuleIsTurnedOffException(context, moduleId, moduleName, commandDetails.name)
				}
				val enabled = commandDataSupplier.isCommandEnabled(properties.guildDbId, commandDetails.id, false)
				if (!enabled) {
					throw CommandIsTurnedOffException(context, commandNameOrAlias)
				}
				val parsedArguments = parseCommandArguments(context, commandDetails, commandArguments)
				parsedArguments.forEach { (key, value) -> context.commandArguments[key] = value }

				commandResponse = executeCommand(context, commandDetails, commandNameOrAlias)

				if (commandResponse.privateMessage) {
					val userId = commandResponse.privateMessageUserId
					directEphemeralUser = event.jda.getUserById(userId)
						?: throw CommandInvocationException("user with id $userId not found", context)
				}
			} catch (ex: CommandInvocationException) {
				if (commandType == CommandType.LEGACY) {
					return
				}
				throw UnexpectedException(ex.context, ex.message)
			}
		} catch (ex: CommandPipelineException) {
			val trackerMessage = exceptionTrackerStore.createTrackerMessage(ex)
			val trackerLink = exceptionTrackerStore.createTrackerLink(ex)
			commandResponse = CommandResponse.ofPublicInteractionMessage(trackerMessage, trackerLink)
			ex.printLogStatement()
		}
		if (directEphemeralUser != null) {
			sendPrivateMessage(event, directEphemeralUser, context, commandResponse)
			return
		}
		deferMessage(event, commandResponse).queue(::startRemovalInteractionThread)
	}

	/**
	 * Sends a private message to a specified user in response to a command execution.
	 *
	 * This function handles the process of sending a private message to the user identified by the provided
	 * `User` object. If the user has their direct messages open, the response
	 * (which may include embedded messages and action components) is sent successfully. If the message sending fails,
	 * an error message is sent to the original command event.
	 *
	 * @param event The event that triggered this handler, containing the context for the command execution.
	 * @param user The user to whom the private message will be sent.
	 * @param context The command context, providing information such as the guild language for localization.
	 * @param response The response generated from executing the command, which may include embedded messages
	 *                 and interactive components.
	 */
	private fun sendPrivateMessage(event: E, user: User, context: CommandContext?, response: CommandResponse) {
		val successMessage = MessageEmbedBuilder(context, i18nBean, jdaColorStoreBean)
			.setDescription(I18nResponseSource.PRIVATE_MESSAGE_SEND)
			.setColor(JdaColor.PRIMARY)
			.build()

		val i18nSource = I18nExceptionSource.EPHEMERAL_UNEXPECTED_EXCEPTION
		val trackerMessage = exceptionTrackerStore.createTrackerMessage(i18nSource)
		val trackerLink = exceptionTrackerStore.createTrackerLink(i18nSource)
		val errorMessage = CommandResponse.ofPrivateInteractionMessage(trackerMessage, trackerLink, user.id)

		val onSuccess: (Message) -> Unit = {
			if (commandType == CommandType.SLASH) {
				val message = CommandResponse.ofPrivateMessage(successMessage, user.id)
				deferMessage(event, message).queue(::startRemovalInteractionThread)
			}
		}
		val onError: (Throwable) -> Unit = { deferMessage(event, errorMessage).queue() }

		val onOpenChannel: (PrivateChannel) -> Unit = {
			val (embedMessages, actionRows) = response
			it.sendMessageEmbeds(embedMessages).addComponents(actionRows).queue(onSuccess, onError)
		}
		user.openPrivateChannel().queue(onOpenChannel, onError)
	}

	/**
	 * Starts the removal interaction thread for a given message. This method schedules the removal of interaction
	 * components from the message after a specified delay.
	 *
	 * @param message The message containing interaction components to be removed.
	 */
	private fun startRemovalInteractionThread(message: Message) {
		if (message.actionRows.isNotEmpty()) {
			interactionRemovalThread.startOnce(remoteInteractionsDelay, TimeUnit.SECONDS, message)
		}
	}

	/**
	 * Parses the command arguments provided in the event.
	 *
	 * @param context The command context.
	 * @param details The command details.
	 * @param arguments The list of arguments passed with the command.
	 * @return A map of command arguments to their parsing data.
	 */
	private fun parseCommandArguments(
		context: CommandContext,
		details: CommandDetails,
		arguments: List<String>
	): Map<CommandArgument, CommandArgumentParsingData> {
		val commandSyntax = createCommandSyntax(context, details)
		val options = LinkedList(arguments)
		val requiredArgs = details.args.filter(CommandArgumentDetails::required)
		if (options.size < requiredArgs.size) {
			throw MismatchCommandArgumentsException(context, details.name, commandSyntax)
		}
		return details.args.associate {
			val optionMapping = options.poll()
			if ((it.options.isNotEmpty() && !it.options.contains(optionMapping))) {
				val syntax = createArgumentsOptionsSyntax(options, context.guildLanguage)
				throw ViolatedCommandArgumentOptionsException(context, it.name, optionMapping, it.options, syntax)
			}
			val argKey = CommandArgument.entries.find { arg -> arg.propName == it.name }
			val type = CommandArgumentType.entries.find { type -> type.name == it.type }
			if (argKey == null || type == null || (optionMapping == null && it.required)) {
				throw MismatchCommandArgumentsException(context, details.name, commandSyntax)
			}
			argKey to CommandArgumentParsingData(optionMapping, type)
		}
	}

	/**
	 * Executes the command with the provided context and command details.
	 *
	 * @param context The command context.
	 * @param details The command details.
	 * @param commandName The name of the command being executed.
	 * @return The response generated by executing the command.
	 */
	private fun executeCommand(context: CommandContext, details: CommandDetails, commandName: String): CommandResponse {
		val commandSyntax = createCommandSyntax(context, details)
		val command = commandsProxyStoreBean.instancesContainer[details.name]
			?: throw CommandIsTurnedOffException(context, commandName)

		val interactiveResponse = try {
			command.execute(context)
		} catch (ex: CommandParserException) {
			throw MismatchCommandArgumentsException(context, details.name, commandSyntax)
		}
		val (embedMessages, actionRows) = interactiveResponse

		val truncatedEmbedMessages = embedMessages.take(maxEmbedMessagesBuffer)
		val truncatedActionRows = actionRows
			.map { ActionRow.of(it.take(maxActionRowComponents)) }
			.take(maxActionRows)

		if (truncatedEmbedMessages.isEmpty() && commandType == CommandType.SLASH) {
			throw CommandInvocationException("not found any slash interaction response", context)
		}
		return interactiveResponse.copy(embedMessages = truncatedEmbedMessages, actionRows = truncatedActionRows)
	}

	/**
	 * Creates a string representation of the command syntax for the help message.
	 *
	 * @param commandContext The command context.
	 * @param command The command details.
	 * @return A string representing the command syntax.
	 */
	private fun createCommandSyntax(commandContext: CommandContext, command: CommandDetails): String {
		val stringJoiner = StringJoiner("")
		stringJoiner.add("\n\n")
		val commandInvokers = arrayOf(command.name, command.alias)
		commandInvokers.forEachIndexed { index, invoker ->
			stringJoiner.add("`${commandContext.prefix}$invoker")
			if (command.argI18nKey != null) {
				val lang = commandContext.guildLanguage
				val argSyntax = i18nBean.tRaw(I18nDynamicMod.ARG_PER_COMMAND_MOD, arrayOf(command.argI18nKey), lang)
				stringJoiner.add(" <$argSyntax>")
			}
			stringJoiner.add("`")
			if (index < commandInvokers.size - 1 && commandInvokers.size != 1) {
				stringJoiner.add("\n")
			}
		}
		return stringJoiner.toString()
	}

	/**
	 * Creates a string representation of the valid options for command arguments.
	 *
	 * @param options The queue of options.
	 * @param lang The language for internationalization.
	 * @return A string listing all valid options.
	 */
	private fun createArgumentsOptionsSyntax(options: Queue<String>, lang: String): String {
		val stringJoiner = StringJoiner("")
		stringJoiner.add("\n\n")
		options.forEachIndexed { index, option ->
			stringJoiner.add("* `$option` - ${i18nBean.tRaw(I18nDynamicMod.ARG_OPTION_MOD, arrayOf(option), lang)}")
			if (index < options.size - 1 && options.size != 1) {
				stringJoiner.add("\n")
			}
		}
		return stringJoiner.toString()
	}

	/**
	 * The type of command this handler processes.
	 */
	protected abstract val commandType: CommandType

	/**
	 * Destroys the interaction removal thread when no longer needed.
	 */
	override fun destroy() = interactionRemovalThread.destroy()

	/**
	 * Determines if the event invocation is forbidden based on certain conditions.
	 *
	 * @param event The event being processed.
	 * @return True if the invocation is forbidden; otherwise, false.
	 */
	protected abstract fun forbiddenInvocationCondition(event: E): Boolean

	/**
	 * Retrieves the guild associated with the event.
	 *
	 * @param event The event being processed.
	 * @return The associated guild, or null if not applicable.
	 */
	protected abstract fun eventGuild(event: E): Guild?

	/**
	 * Checks if the event invocation details are forbidden based on guild properties.
	 *
	 * @param event The event being processed.
	 * @param properties The command properties for the guild.
	 * @return True if forbidden; otherwise, false.
	 */
	protected open fun forbiddenInvocationDetails(event: E, properties: GuildCommandProperties) = false

	/**
	 * Extracts the command name and its arguments from the event.
	 *
	 * @param event The event being processed.
	 * @return A pair containing the command name and a list of arguments.
	 */
	protected abstract fun commandNameAndArguments(event: E): Pair<String, List<String>>

	/**
	 * Creates the command context from the event and the guild properties.
	 *
	 * @param event The event being processed.
	 * @param properties The command properties for the guild.
	 * @return The command context created from the event.
	 */
	protected abstract fun createCommandContext(event: E, properties: GuildCommandProperties): CommandContext

	/**
	 * Defers a message based on the event and the command response.
	 *
	 * @param event The event being processed.
	 * @param response The response generated from executing the command.
	 * @return A RestAction that sends the deferred message.
	 */
	protected abstract fun deferMessage(event: E, response: CommandResponse): RestAction<Message>
}
