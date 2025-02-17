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

internal abstract class CommandEventHandler<E : Event>(
	private val commandEventHandlerEnvironment: CommandEventHandlerEnvironment,
) : UnifiedCommandHandler<E>() {
	private val i18n = commandEventHandlerEnvironment.i18n
	private val commandsCache = commandEventHandlerEnvironment.commandsCache
	protected val environment = commandEventHandlerEnvironment.environment

	private val propertiesList = listOf(
		GuildProperty.DB_ID,
		GuildProperty.LANGUAGE_TAG,
		GuildProperty.LEGACY_PREFIX,
		GuildProperty.SLASH_ENABLED,
		GuildProperty.DJ_ROLE_NAME,
		GuildProperty.MUSIC_TEXT_CHANNEL_ID,
		GuildProperty.SUPPRESS_RESPONSE_NOTIFICATIONS,
	)

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
					properties = commandEventHandlerEnvironment.guildEnvironment.getGuildMultipleProperties(
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
				val mergedCommand = Command.rawCommandToDotFormat(commandNameOrAlias)
				val commandDetails = Command.entries
					.find { it.textKey == mergedCommand }
					?: throw CommandInvocationException("command: \"$mergedCommand\" could not be found")
				context = if (properties != null) {
					val guildContext = createGuildCommandContext(event, commandDetails.textKey, properties)
					val dbId = properties.getProperty<BigInteger>(GuildProperty.DB_ID)
					val module = commandDetails.module
					if (commandEventHandlerEnvironment.moduleDataSupplier.isDisabled(module.dbId, dbId)) {
						val moduleName = i18n.t(module, guildContext.language)
						throw ModuleIsTurnedOffException(
							guildContext,
							module.name,
							moduleName,
							commandDetails.name
						)
					}
					if (commandDataSupplier.isCommandDisabled(
							dbId,
							commandDetails.dbId,
							commandType == CommandType.SLASH
						)
					) {
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
						is GlobalCommandHandler -> command.executeGlobal(
							context as GlobalCommandContext,
							commandResponse,
						)
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

	private fun sendResponse(
		event: E,
		response: CommandResponse,
		context: CommandBaseContext?,
		privateUserId: Long?,
	) {
		val looselyTransportHandler = commandEventHandlerEnvironment.looselyTransportHandler
		var directEphemeralUser: User? = null
		val truncatedResponse = try {
			if (response.embedMessages.isEmpty() && commandType == CommandType.SLASH) {
				throw UnexpectedException(context, "response should have at least one embed message")
			}
			if (privateUserId != null) {
				directEphemeralUser = event.jda.getUserById(privateUserId)
					?: throw UnexpectedException(context, "ephemeral user cannot be null")
			}
			looselyTransportHandler.truncateComponents(response)
		} catch (ex: CommandPipelineException) {
			createExceptionMessage(ex)
		}
		if (directEphemeralUser != null && context != null) {
			sendPrivateMessage(event, directEphemeralUser, context, response)
			return
		}
		val onMessageSend: (Message) -> Unit = {
			if (truncatedResponse.disposeComponents) {
				looselyTransportHandler.startRemovalInteractionThread(it)
			}
			truncatedResponse.afterSendAction(it)
		}
		deferMessage(
			event,
			truncatedResponse,
			privateMessage = false,
			context?.suppressResponseNotifications,
		).queue(onMessageSend)
	}

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

	private fun sendPrivateMessage(
		event: E,
		user: User,
		context: CommandBaseContext?,
		response: CommandResponse,
	) {
		val looselyTransportHandler = commandEventHandlerEnvironment.looselyTransportHandler
		val exceptionTrackerStore = commandEventHandlerEnvironment.exceptionTrackerHandler
		val successMessage =
			MessageEmbedBuilder(i18n, commandEventHandlerEnvironment.jdaColorStore, context)
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

		val onError: (Throwable) -> Unit = {
			deferMessage(
				event,
				errorMessage,
				privateMessage = true,
				context?.suppressResponseNotifications
			).queue()
		}
		user.openPrivateChannel().queue({
			it.sendMessageEmbeds(response.embedMessages).addComponents(response.actionRows)
				.queue({ privateMessage ->
					if (commandType == CommandType.SLASH) {
						val message = CommandResponse.Builder().addEmbedMessages(successMessage).build()
						deferMessage(
							event,
							message,
							privateMessage = true,
							context?.suppressResponseNotifications
						).queue { looselyTransportHandler.startRemovalInteractionThread(privateMessage) }
					}
				}, onError)
		}, onError)
	}

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
				throw ViolatedCommandArgumentOptionsException(
					context,
					argsDescription,
					optionMapping,
					argOptions,
					syntax
				)
			}
			if (optionMapping == null && it.required) {
				throw MismatchCommandArgumentsException(context, commandSyntax)
			}
			optionMapping
		}
	}

	private fun createCommandSyntax(context: CommandBaseContext, command: Command): String {
		val stringJoiner = StringJoiner("")
		stringJoiner.add("\n")
		stringJoiner.add("\n`${command.parseWithPrefix(context)}")
		if (command.argumentsDefinition != null) {
			val lang = context.language
			val argSyntax = i18n.t(command.argumentsDefinition!!, lang)
			stringJoiner.add(" <$argSyntax>")
		}
		stringJoiner.add("`")
		return stringJoiner.toString()
	}

	private fun createArgumentsOptionsSyntax(options: List<ArgumentOption>, lang: String): String {
		val stringJoiner = StringJoiner("")
		stringJoiner.add("\n")
		options.forEach { stringJoiner.add("\n* `${it.textKey}` - ${i18n.t(it, lang)}") }
		return stringJoiner.toString()
	}
}
