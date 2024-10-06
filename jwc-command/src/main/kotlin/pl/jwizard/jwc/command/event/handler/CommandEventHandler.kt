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
import net.dv8tion.jda.api.requests.RestAction
import pl.jwizard.jwc.command.CommandsCacheBean
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.event.CommandType
import pl.jwizard.jwc.command.event.arg.CommandArgumentParsingData
import pl.jwizard.jwc.command.event.arg.CommandArgumentType
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.event.exception.CommandInvocationException
import pl.jwizard.jwc.command.event.exception.CommandParserException
import pl.jwizard.jwc.command.event.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.command.refer.CommandArgument
import pl.jwizard.jwc.command.reflect.CommandArgumentDetails
import pl.jwizard.jwc.command.reflect.CommandDetails
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.core.exception.spi.ExceptionTrackerStore
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.source.I18nDynamicMod
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwc.exception.command.CommandIsTurnedOffException
import pl.jwizard.jwc.exception.command.MismatchCommandArgumentsException
import pl.jwizard.jwc.exception.command.ModuleIsTurnedOffException
import pl.jwizard.jwc.exception.command.ViolatedCommandArgumentOptionsException
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Abstract base class for handling command events in the Discord bot.
 *
 * This class is responsible for processing incoming events, executing commands, managing exceptions, and responding
 * to the user appropriately.
 *
 * @property commandDataSupplier The supplier for command data.
 * @property moduleDataSupplier The supplier for module data.
 * @property commandsCacheBean The bean for storing command cache.
 * @property exceptionTrackerStore The bean for tracking exceptions.
 * @property i18nBean The bean for internationalization.
 * @property environmentBean The bean for environment properties.
 * @property jdaColorStoreBean Accesses to JDA defined colors for embed messages.
 * @author Miłosz Gilga
 */
abstract class CommandEventHandler<E : Event>(
	private val commandDataSupplier: CommandDataSupplier,
	private val moduleDataSupplier: ModuleDataSupplier,
	private val commandsCacheBean: CommandsCacheBean,
	private val exceptionTrackerStore: ExceptionTrackerStore,
	private val i18nBean: I18nBean,
	private val environmentBean: EnvironmentBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
	private val looselyTransportHandlerBean: LooselyTransportHandlerBean,
) : ListenerAdapter() {

	/**
	 * Initializes the command pipeline and performs the command based on the event. This method handles the main logic
	 * of checking command conditions, parsing arguments, and executing the command.
	 *
	 * @param event The event that triggered this handler.
	 */
	protected fun initPipelineAndPerformCommand(event: E) {
		var commandResponse: TFutureResponse
		var context: CommandContext? = null
		try {
			try {
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

				val commandSyntax = createCommandSyntax(context, commandDetails)
				val command = commandsCacheBean.instancesContainer[commandDetails.name]
					?: throw CommandIsTurnedOffException(context, commandNameOrAlias)

				commandResponse = CompletableFuture<CommandResponse>()
				try {
					command.execute(context, commandResponse)
				} catch (ex: CommandParserException) {
					throw MismatchCommandArgumentsException(context, commandDetails.name, commandSyntax)
				}
			} catch (ex: CommandInvocationException) {
				if (commandType == CommandType.LEGACY) {
					return
				}
				throw UnexpectedException(ex.context, ex.message)
			}
		} catch (ex: CommandPipelineExceptionHandler) {
			commandResponse = CompletableFuture()
			commandResponse.complete(createExceptionMessage(ex))
		}
		commandResponse.thenAccept { sendResponse(event, it, context) }
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
	 */
	private fun sendResponse(event: E, response: CommandResponse, context: CommandContext?) {
		var directEphemeralUser: User? = null
		val truncatedResponse = try {
			if (response.embedMessages.isEmpty() && commandType == CommandType.SLASH) {
				throw CommandInvocationException("response should have at least one embed message")
			}
			if (response.privateMessage && response.privateMessageUserId != null) {
				directEphemeralUser = event.jda.getUserById(response.privateMessageUserId!!)
					?: throw CommandInvocationException("ephemeral user cannot be null")
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
		deferMessage(event, truncatedResponse).queue(onMessageSend)
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
		val successMessage = MessageEmbedBuilder(i18nBean, jdaColorStoreBean, context)
			.setDescription(I18nResponseSource.PRIVATE_MESSAGE_SEND)
			.setColor(JdaColor.PRIMARY)
			.build()

		val i18nSource = I18nExceptionSource.EPHEMERAL_UNEXPECTED_EXCEPTION
		val trackerMessage = exceptionTrackerStore.createTrackerMessage(i18nSource)
		val trackerLink = exceptionTrackerStore.createTrackerLink(i18nSource)

		val errorMessage = CommandResponse.Builder()
			.addEmbedMessages(trackerMessage)
			.addActionRows(trackerLink)
			.asPrivateMessage(user.idLong)
			.build()

		val onSuccess: (Message) -> Unit = {
			if (commandType == CommandType.SLASH) {
				val message = CommandResponse.Builder()
					.addEmbedMessages(successMessage)
					.asPrivateMessage(user.idLong)
					.build()
				deferMessage(event, message).queue(looselyTransportHandlerBean::startRemovalInteractionThread)
			}
		}
		val onError: (Throwable) -> Unit = { deferMessage(event, errorMessage).queue() }

		val onOpenChannel: (PrivateChannel) -> Unit = {
			it.sendMessageEmbeds(response.embedMessages).addComponents(response.actionRows).queue(onSuccess, onError)
		}
		user.openPrivateChannel().queue(onOpenChannel, onError)
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
	 * @return A RestAction that sends the deferred message.
	 */
	protected abstract fun deferMessage(event: E, response: CommandResponse): RestAction<Message>
}
