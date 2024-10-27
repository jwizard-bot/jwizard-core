/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.handler

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.RestAction
import pl.jwizard.jwc.command.CommandType
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.arg.CommandArgumentParsingData
import pl.jwizard.jwc.command.arg.CommandArgumentType
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.exception.CommandInvocationException
import pl.jwizard.jwc.command.exception.CommandParserException
import pl.jwizard.jwc.command.refer.CommandArgument
import pl.jwizard.jwc.command.reflect.CommandArgumentDetails
import pl.jwizard.jwc.command.reflect.CommandDetails
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwc.exception.command.CommandIsTurnedOffException
import pl.jwizard.jwc.exception.command.MismatchCommandArgumentsException
import pl.jwizard.jwc.exception.command.ModuleIsTurnedOffException
import pl.jwizard.jwc.exception.command.ViolatedCommandArgumentOptionsException
import pl.jwizard.jwl.i18n.source.I18nDynamicMod
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Abstract base class for handling command events in the Discord bot.
 *
 * This class is responsible for processing incoming events, executing commands, managing exceptions, and responding
 * to the user appropriately.
 *
 * @property commandEventHandlerEnvironmentBean Stored all beans for command event handler.
 * @author Miłosz Gilga
 */
abstract class CommandEventHandler<E : Event>(
	private val commandEventHandlerEnvironmentBean: CommandEventHandlerEnvironmentBean,
) : ListenerAdapter() {

	private val i18nBean = commandEventHandlerEnvironmentBean.i18nBean

	/**
	 * Initializes the command pipeline and performs the command based on the event. This method handles the main logic
	 * of checking command conditions, parsing arguments, and executing the command.
	 *
	 * @param event The event that triggered this handler.
	 */
	protected fun initPipelineAndPerformCommand(event: E) {
		var commandResponse: TFutureResponse
		var context: CommandContext? = null
		var privateMessageUserId: Long? = null
		try {
			try {
				val commandDataSupplier = commandEventHandlerEnvironmentBean.commandDataSupplier
				val commandsCacheBean = commandEventHandlerEnvironmentBean.commandsCacheBean
				if (forbiddenInvocationCondition(event)) {
					throw CommandInvocationException("forbidden invocation condition")
				}
				val guild = eventGuild(event) ?: throw CommandInvocationException("guild is null")
				val properties = commandDataSupplier.getCommandPropertiesFromGuild(guild.idLong)
					?: throw CommandInvocationException("properties not exists")

				if (forbiddenInvocationDetails(event, properties)) {
					throw CommandInvocationException("forbidden invocation details")
				}
				val (commandNameOrAlias, commandArguments) = commandNameAndArguments(event)

				val commandDetails = commandsCacheBean.commands[commandNameOrAlias]
					?: throw CommandInvocationException("command by command name could not be found")
				context = createCommandContext(event, commandDetails.name, properties)

				val (moduleId, isEnabled) = commandEventHandlerEnvironmentBean.moduleDataSupplier
					.isEnabled(commandDetails.name, properties.guildDbId)
					?: throw CommandInvocationException("module by command name could not be found", context)
				if (!isEnabled) {
					val moduleName = i18nBean.tRaw(I18nDynamicMod.MODULES_MOD, arrayOf(moduleId), context.guildLanguage)
					throw ModuleIsTurnedOffException(context, moduleId, moduleName, commandDetails.name)
				}
				val enabled = commandDataSupplier
					.isCommandEnabled(properties.guildDbId, commandDetails.id, commandType == CommandType.SLASH)
				if (!enabled) {
					throw CommandIsTurnedOffException(context)
				}
				val parsedArguments = parseCommandArguments(context, commandDetails, commandArguments)
				parsedArguments.forEach { (key, value) -> context.commandArguments[key] = value }

				val commandSyntax = createCommandSyntax(context, commandDetails)
				val command = commandsCacheBean.instancesContainer[commandDetails.name]
					?: throw CommandIsTurnedOffException(context)

				commandResponse = CompletableFuture<CommandResponse>()
				try {
					privateMessageUserId = command.isPrivate(context)
					deferAction(event, privateMessageUserId != null)
					command.execute(context, commandResponse)
				} catch (ex: CommandParserException) {
					throw MismatchCommandArgumentsException(context, commandSyntax)
				}
			} catch (ex: CommandInvocationException) {
				if (commandType == CommandType.PREFIX) {
					return
				}
				throw UnexpectedException(ex.context, ex.message)
			}
		} catch (ex: CommandPipelineExceptionHandler) {
			deferAction(event, privateMessageUserId != null)
			commandResponse = CompletableFuture()
			commandResponse.complete(createExceptionMessage(ex))
		}
		commandResponse.thenAccept { sendResponse(event, it, context, privateMessageUserId) }
	}

	/**
	 * Sends a command response message after processing an event.
	 *
	 * This method takes the command response and sends it to the user, truncating embedded messages and action rows if
	 * they exceed the predefined limits. If the response is set to be a private message, it will send the message
	 * directly to the user. Otherwise, it sends it to the public channel.
	 *
	 * @param event The event that triggered this handler.
	 * @param response The response generated from executing the command.
	 * @param context Optional context for the command, containing additional execution details.
	 * @param privateUserId User id used to send private message. If it is `null`, message is public.
	 */
	private fun sendResponse(event: E, response: CommandResponse, context: CommandContext?, privateUserId: Long?) {
		val looselyTransportHandlerBean = commandEventHandlerEnvironmentBean.looselyTransportHandlerBean
		var directEphemeralUser: User? = null
		val truncatedResponse = try {
			if (response.embedMessages.isEmpty() && commandType == CommandType.SLASH) {
				throw UnexpectedException(context, "response should have at least one embed message")
			}
			if (privateUserId != null) {
				directEphemeralUser = event.jda.getUserById(privateUserId)
					?: throw UnexpectedException(context, "ephemeral user cannot be null")
			}
			looselyTransportHandlerBean.truncateComponents(response)
		} catch (ex: CommandPipelineExceptionHandler) {
			createExceptionMessage(ex)
		}
		if (directEphemeralUser != null && context != null) {
			sendPrivateMessage(event, directEphemeralUser, context, response)
			return
		}
		val onMessageSend: (Message) -> Unit = {
			if (truncatedResponse.disposeComponents) {
				looselyTransportHandlerBean.startRemovalInteractionThread(it)
			}
			truncatedResponse.afterSendAction(it)
		}
		deferMessage(event, truncatedResponse, privateMessage = false).queue(onMessageSend)
	}

	/**
	 * Creates a command response that includes details about an exception.
	 *
	 * This method is used to generate a [CommandResponse] object that contains information about an exception
	 * encountered during command execution. The exception details are formatted and stored for tracking purposes.
	 *
	 * @param ex The exception thrown during command execution.
	 * @return A [CommandResponse] containing the formatted exception message and optional tracking link.
	 */
	private fun createExceptionMessage(ex: CommandPipelineExceptionHandler): CommandResponse {
		ex.printLogStatement()
		val exceptionTrackerStore = commandEventHandlerEnvironmentBean.exceptionTrackerStore
		val trackerMessage = exceptionTrackerStore.createTrackerMessage(ex)
		val trackerLink = exceptionTrackerStore.createTrackerLink(ex)
		return CommandResponse.Builder()
			.addEmbedMessages(trackerMessage)
			.addActionRows(trackerLink)
			.build()
	}

	/**
	 * Sends a private message to a specified user in response to a command execution.
	 *
	 * This function handles the process of sending a private message to the user identified by the provided [User]
	 * object. If the user has their direct messages open, the response (which may include embedded messages and action
	 * components) is sent successfully. If the message sending fails, an error message is sent to the original command
	 * event.
	 *
	 * @param event The event that triggered this handler, containing the context for the command execution.
	 * @param user The user to whom the private message will be sent.
	 * @param context The command context, providing information such as the guild language for localization.
	 * @param response The response generated from executing the command, which may include embedded messages and
	 *        interactive components.
	 */
	private fun sendPrivateMessage(event: E, user: User, context: CommandContext?, response: CommandResponse) {
		val looselyTransportHandlerBean = commandEventHandlerEnvironmentBean.looselyTransportHandlerBean
		val exceptionTrackerStore = commandEventHandlerEnvironmentBean.exceptionTrackerStore
		val successMessage = MessageEmbedBuilder(i18nBean, commandEventHandlerEnvironmentBean.jdaColorStoreBean, context)
			.setTitle(I18nResponseSource.PRIVATE_MESSAGE_SEND)
			.setDescription(I18nResponseSource.CHECK_INBOX)
			.setColor(JdaColor.PRIMARY)
			.build()

		val i18nSource = I18nExceptionSource.EPHEMERAL_UNEXPECTED_EXCEPTION
		val trackerMessage = exceptionTrackerStore.createTrackerMessage(i18nSource)
		val trackerLink = exceptionTrackerStore.createTrackerLink(i18nSource)

		val errorMessage = CommandResponse.Builder()
			.addEmbedMessages(trackerMessage)
			.addActionRows(trackerLink)
			.build()

		val onSuccess: (Message) -> Unit = { privateMessage ->
			if (commandType == CommandType.SLASH) {
				val message = CommandResponse.Builder().addEmbedMessages(successMessage).build()
				deferMessage(event, message, privateMessage = true)
					.queue { looselyTransportHandlerBean.startRemovalInteractionThread(privateMessage) }
			}
		}
		val onError: (Throwable) -> Unit = { deferMessage(event, errorMessage, privateMessage = true).queue() }
		user.openPrivateChannel().queue({
			it.sendMessageEmbeds(response.embedMessages).addComponents(response.actionRows).queue(onSuccess, onError)
		}, onError)
	}

	/**
	 * Parses the command arguments passed in the event and checks if they are valid.
	 *
	 * This method ensures that the arguments passed match the required command arguments and their respective types,
	 * handling optional and required arguments as defined in the command details.
	 *
	 * @param context The command context, including execution details like language and guild ID.
	 * @param details The command details, including its argument structure.
	 * @param arguments The list of arguments passed in the command.
	 * @return A map of parsed command arguments and their associated data.
	 */
	private fun parseCommandArguments(
		context: CommandContext,
		details: CommandDetails,
		arguments: List<String>,
	): Map<CommandArgument, CommandArgumentParsingData> {
		val commandSyntax = createCommandSyntax(context, details)
		val options = LinkedList(arguments)
		val requiredArgs = details.args.filter(CommandArgumentDetails::required)
		if (options.size < requiredArgs.size) {
			throw MismatchCommandArgumentsException(context, commandSyntax)
		}
		return details.args.associate {
			val optionMapping: String? = options.poll()
			if (it.options.isNotEmpty() && !it.options.contains(optionMapping)) {
				val syntax = createArgumentsOptionsSyntax(context.commandName, it.options, context.guildLanguage)
				val argsDescription = i18nBean.tRaw(I18nDynamicMod.ARGS_MOD, arrayOf(it.name), context.guildLanguage)
				throw ViolatedCommandArgumentOptionsException(context, argsDescription, optionMapping, it.options, syntax)
			}
			val argKey = CommandArgument.entries.find { arg -> arg.propName == it.name }
			val type = CommandArgumentType.entries.find { type -> type.name == it.type }
			if (argKey == null || type == null || (optionMapping == null && it.required)) {
				throw MismatchCommandArgumentsException(context, commandSyntax)
			}
			argKey to CommandArgumentParsingData(optionMapping, type, it.required)
		}
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
	private fun createArgumentsOptionsSyntax(commandName: String, options: List<String>, lang: String): String {
		val stringJoiner = StringJoiner("")
		stringJoiner.add("\n\n")
		options.forEachIndexed { index, option ->
			val optionName = i18nBean.tRaw(I18nDynamicMod.ARG_OPTION_MOD, arrayOf(commandName, option), lang)
			stringJoiner.add("* `$option` - $optionName\n")
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
	 * @param command Definition of the command on which the event was invoked.
	 * @param properties The command properties for the guild.
	 * @return The command context created from the event.
	 */
	protected abstract fun createCommandContext(
		event: E,
		command: String,
		properties: GuildCommandProperties
	): CommandContext

	/**
	 * Defers a message based on the event and the command response.
	 *
	 * @param event The event being processed.
	 * @param response The response generated from executing the command.
	 * @param privateMessage The value defined, if sending message should be private or public.
	 * @return A RestAction that sends the deferred message.
	 */
	protected abstract fun deferMessage(event: E, response: CommandResponse, privateMessage: Boolean): RestAction<Message>

	/**
	 * Defers an action for the given event, which can be configured to be either public or private (ephemeral).
	 * This method can be overridden to customize the behavior based on the event type and context.
	 *
	 * @param event The event that triggered the action, typically containing the necessary context such as user, guild,
	 *        or message details.
	 * @param privateMessage A boolean indicating whether the response should be ephemeral (private) or public. If true,
	 *        the response will be sent as a private message (ephemeral); if false, it will be visible to everyone.
	 */
	protected open fun deferAction(event: E, privateMessage: Boolean) {}
}
