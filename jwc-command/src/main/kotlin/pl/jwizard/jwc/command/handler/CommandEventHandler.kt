/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.handler

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.Event
import pl.jwizard.jwc.command.CommandType
import pl.jwizard.jwc.command.GlobalCommandHandler
import pl.jwizard.jwc.command.GuildCommandHandler
import pl.jwizard.jwc.command.context.ArgumentContext
import pl.jwizard.jwc.command.context.GlobalCommandContext
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.exception.CommandInvocationException
import pl.jwizard.jwc.command.exception.CommandParserException
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwc.exception.command.CommandIsTurnedOffException
import pl.jwizard.jwc.exception.command.MismatchCommandArgumentsException
import pl.jwizard.jwc.exception.command.ModuleIsTurnedOffException
import pl.jwizard.jwc.exception.command.ViolatedCommandArgumentOptionsException
import pl.jwizard.jwl.command.ArgumentOption
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.property.AppBaseProperty
import java.math.BigInteger
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Abstract base class for handling command events in the Discord bot.
 *
 * This class is responsible for processing incoming events, executing commands, managing exceptions, and responding
 * to the user appropriately.
 *
 * @property commandEventHandlerEnvironment Stored all beans for command event handler.
 * @author Miłosz Gilga
 */
abstract class CommandEventHandler<E : Event>(
	private val commandEventHandlerEnvironment: CommandEventHandlerEnvironmentBean,
) : UnifiedCommandHandler<E>() {

	private val i18n = commandEventHandlerEnvironment.i18n
	private val commandsCache = commandEventHandlerEnvironment.commandsCache
	protected val environment = commandEventHandlerEnvironment.environment

	/**
	 * Properties retrieved in single query at startup command pipeline.
	 */
	private val propertiesList = listOf(
		GuildProperty.DB_ID,
		GuildProperty.LANGUAGE_TAG,
		GuildProperty.LEGACY_PREFIX,
		GuildProperty.SLASH_ENABLED,
		GuildProperty.DJ_ROLE_NAME,
		GuildProperty.MUSIC_TEXT_CHANNEL_ID,
		GuildProperty.SUPPRESS_RESPONSE_NOTIFICATIONS,
	)

	/**
	 * Initializes the command pipeline and performs the command based on the event. This method handles the main logic
	 * of checking command conditions, parsing arguments, and executing the command.
	 *
	 * @param event The event that triggered this handler.
	 * @param fromGuild `true`, if event comes from guild, otherwise `false` (ex. private channel).
	 */
	protected fun initPipelineAndPerformCommand(event: E, fromGuild: Boolean) {
		var commandResponse: TFutureResponse
		var context: ArgumentContext? = null
		var privateMessageUserId: Long? = null
		try {
			try {
				val commandDataSupplier = commandEventHandlerEnvironment.commandDataSupplier
				var properties: GuildMultipleProperties? = null
				val prefix = if (fromGuild) {
					val guild = eventGuild(event) ?: throw CommandInvocationException("guild is null")
					properties = commandEventHandlerEnvironment.environment.getGuildMultipleProperties(
						guildProperties = propertiesList,
						guildId = guild.idLong,
					)
					if (forbiddenInvocationDetails(event, properties)) {
						throw CommandInvocationException("forbidden invocation details")
					}
					properties.getProperty<String>(GuildProperty.LEGACY_PREFIX)
				} else {
					environment.getProperty<String>(AppBaseProperty.GUILD_LEGACY_PREFIX)
				}
				val (commandNameOrAlias, commandArguments) = commandNameAndArguments(event, prefix)
				val mergedCommand = commandNameOrAlias.replace(" ", ".")
				val commandDetails = Command.entries
					.find { it.textKey == mergedCommand }
					?: throw CommandInvocationException("command by command name: \"$mergedCommand\" could not be found")

				context = if (properties != null) {
					val guildContext = createGuildCommandContext(event, commandDetails.textKey, properties)
					val dbId = properties.getProperty<BigInteger>(GuildProperty.DB_ID)
					val module = commandDetails.module
					if (commandEventHandlerEnvironment.moduleDataSupplier.isDisabled(module.dbId, dbId)) {
						val moduleName = i18n.t(module, guildContext.language)
						throw ModuleIsTurnedOffException(guildContext, module.name, moduleName, commandDetails.name)
					}
					if (commandDataSupplier.isCommandDisabled(dbId, commandDetails.dbId, commandType == CommandType.SLASH)) {
						throw CommandIsTurnedOffException(guildContext)
					}
					guildContext
				} else {
					createGlobalCommandContext(event, commandDetails.textKey)!!
				}
				val parsedArguments = parseCommandArguments(context, commandDetails, commandArguments)
				parsedArguments.forEach { (key, value) -> context.commandArguments[key] = value }

				val commandSyntax = createCommandSyntax(context, commandDetails)
				val cache = if (fromGuild) {
					commandsCache.guildCommandInstances
				} else {
					commandsCache.globalCommandInstances
				}
				val command = cache[commandDetails] ?: throw CommandIsTurnedOffException(context)

				commandResponse = CompletableFuture<CommandResponse>()
				try {
					when (command) {
						is GuildCommandHandler -> {
							context as GuildCommandContext
							privateMessageUserId = command.isPrivate(context)
							deferAction(event, privateMessageUserId != null)
							command.execute(context, commandResponse)
						}
						is GlobalCommandHandler -> command.executeGlobal(context as GlobalCommandContext, commandResponse)
					}
				} catch (ex: CommandParserException) {
					throw MismatchCommandArgumentsException(context, commandSyntax)
				}
			} catch (ex: CommandInvocationException) {
				if (commandType == CommandType.PREFIX) {
					return
				}
				throw UnexpectedException(ex.context, ex.message)
			}
		} catch (ex: CommandPipelineException) {
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
	private fun sendResponse(event: E, response: CommandResponse, context: CommandBaseContext?, privateUserId: Long?) {
		val looselyTransportHandlerBean = commandEventHandlerEnvironment.looselyTransportHandler
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
		} catch (ex: CommandPipelineException) {
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
		deferMessage(event, truncatedResponse, privateMessage = false, context?.suppressResponseNotifications)
			.queue(onMessageSend)
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
	private fun createExceptionMessage(ex: CommandPipelineException): CommandResponse {
		ex.printLogStatement()
		val exceptionTrackerStore = commandEventHandlerEnvironment.exceptionTrackerHandler
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
	 * @param context The command context, providing command invocation information.
	 * @param response The response generated from executing the command, which may include embedded messages and
	 *        interactive components.
	 */
	private fun sendPrivateMessage(event: E, user: User, context: CommandBaseContext?, response: CommandResponse) {
		val looselyTransportHandlerBean = commandEventHandlerEnvironment.looselyTransportHandler
		val exceptionTrackerStore = commandEventHandlerEnvironment.exceptionTrackerHandler
		val successMessage = MessageEmbedBuilder(i18n, commandEventHandlerEnvironment.jdaColorStore, context)
			.setTitle(I18nResponseSource.PRIVATE_MESSAGE_SEND)
			.setDescription(I18nResponseSource.CHECK_INBOX)
			.setColor(JdaColor.PRIMARY)
			.build()

		val i18nSource = I18nExceptionSource.EPHEMERAL_UNEXPECTED_EXCEPTION
		val trackerMessage = exceptionTrackerStore.createTrackerMessage(i18nSource, context)
		val trackerLink = exceptionTrackerStore.createTrackerLink(i18nSource, context)

		val errorMessage = CommandResponse.Builder()
			.addEmbedMessages(trackerMessage)
			.addActionRows(trackerLink)
			.build()

		val onSuccess: (Message) -> Unit = { privateMessage ->
			if (commandType == CommandType.SLASH) {
				val message = CommandResponse.Builder().addEmbedMessages(successMessage).build()
				deferMessage(event, message, privateMessage = true, context?.suppressResponseNotifications)
					.queue { looselyTransportHandlerBean.startRemovalInteractionThread(privateMessage) }
			}
		}
		val onError: (Throwable) -> Unit = {
			deferMessage(event, errorMessage, privateMessage = true, context?.suppressResponseNotifications).queue()
		}
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
	 * @param context The command context, including execution details.
	 * @param details The command details, including its argument structure.
	 * @param arguments The list of arguments passed in the command.
	 * @return A map of parsed command arguments and their associated value.
	 */
	private fun parseCommandArguments(
		context: CommandBaseContext,
		details: Command,
		arguments: List<String>,
	): Map<Argument, String?> {
		val commandSyntax = createCommandSyntax(context, details)
		val options = LinkedList(arguments)
		val requiredArgs = details.exactArguments.filter(Argument::required)
		if (options.size < requiredArgs.size) {
			throw MismatchCommandArgumentsException(context, commandSyntax)
		}
		return details.exactArguments.associateWith {
			val argOptions = it.options.map(ArgumentOption::textKey)
			val optionMapping: String? = options.poll()
			if (argOptions.isNotEmpty() && !argOptions.contains(optionMapping)) {
				val syntax = createArgumentsOptionsSyntax(it.options, context.language)
				val argsDescription = i18n.t(it, context.language)
				throw ViolatedCommandArgumentOptionsException(context, argsDescription, optionMapping, argOptions, syntax)
			}
			if (optionMapping == null && it.required) {
				throw MismatchCommandArgumentsException(context, commandSyntax)
			}
			optionMapping
		}
	}

	/**
	 * Creates a string representation of the command syntax for the help message.
	 *
	 * @param context The command context.
	 * @param command The command details.
	 * @return A string representing the command syntax.
	 */
	private fun createCommandSyntax(context: CommandBaseContext, command: Command): String {
		val stringJoiner = StringJoiner("")
		stringJoiner.add("\n")
		stringJoiner.add("\n`${context.prefix}${context.commandName}")
		if (command.argumentsDefinition != null) {
			val lang = context.language
			val argSyntax = i18n.t(command.argumentsDefinition!!, lang)
			stringJoiner.add(" <$argSyntax>")
		}
		stringJoiner.add("`")
		return stringJoiner.toString()
	}

	/**
	 * Creates a string representation of the valid options for command arguments.
	 *
	 * @param options The queue of options.
	 * @param lang The language for internationalization.
	 * @return A string listing all valid options.
	 */
	private fun createArgumentsOptionsSyntax(options: List<ArgumentOption>, lang: String): String {
		val stringJoiner = StringJoiner("")
		stringJoiner.add("\n")
		options.forEach { stringJoiner.add("\n* `${it.textKey}` - ${i18n.t(it, lang)}") }
		return stringJoiner.toString()
	}
}
