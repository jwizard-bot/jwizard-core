/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.misc

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.CommandBase
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.command.GlobalCommandHandler
import pl.jwizard.jwc.command.context.GlobalCommandContext
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.util.mdBold
import pl.jwizard.jwc.core.util.mdCode
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import java.util.*

/**
 * Command class responsible for handling the `Help` command. This command provides a list of available commands in
 * the bot and helps the user navigate through them.
 *
 * @property commandDataSupplier Supplies metadata and definitions for bot commands.
 * @property moduleDataSupplier Supplies metadata and definitions for command modules.
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.HELP)
class HelpCmd(
	private val commandDataSupplier: CommandDataSupplier,
	private val moduleDataSupplier: ModuleDataSupplier,
	commandEnvironment: CommandEnvironmentBean,
) : CommandBase(commandEnvironment), GlobalCommandHandler {

	/**
	 * Executes the `Help` command in a guild-specific context. Filters out disabled commands and modules for the guild
	 * and displays the remaining ones in a paginated embed format.
	 *
	 * @param context The guild command context, including guild and user details.
	 * @param response The future response object used to send the result asynchronously.
	 */
	override fun execute(context: GuildCommandContext, response: TFutureResponse) {
		val disabledCommands = commandDataSupplier.getDisabledGuildCommands(context.guildDbId, context.isSlashEvent)
		val disabledModules = moduleDataSupplier.getDisabledGuildModules(context.guildDbId)

		val guildCommands = Command.entries.filter { it.dbId !in disabledCommands && it.module.dbId !in disabledModules }
		executeCommand(guildCommands, context, response)
	}

	/**
	 * Executes the `Help` command in a global context. Displays all available commands without guild-specific filtering.
	 *
	 * @param context The global command context, including user and interaction details.
	 * @param response The future response object used to send the result asynchronously.
	 */
	override fun executeGlobal(context: GlobalCommandContext, response: TFutureResponse) =
		executeCommand(Command.entries, context, response)

	/**
	 * Checks whether the command should be executed in private mode. If the command is executed with a "private"
	 * argument set to true, it returns the author's ID to restrict visibility.
	 *
	 * @param context The context of the command execution.
	 * @return The author's ID if the command is private, or null if it is not private.
	 */
	override fun isPrivate(context: GuildCommandContext): Long? {
		val isPrivate = context.getNullableArg<Boolean>(Argument.PRIVATE)
		return if (isPrivate == true) context.author.idLong else null
	}

	/**
	 * Handles the actual execution of the `Help` command. Creates paginated embeds with available commands and sends
	 * them as a response.
	 *
	 * @param commands A list of commands to include in the response.
	 * @param context The command execution context.
	 * @param response The future response object used to send the result asynchronously.
	 */
	private fun executeCommand(commands: List<Command>, context: CommandBaseContext, response: TFutureResponse) {
		val paginator = createPaginator(context, createHelpComponents(context, commands))
		val row = paginator.createPaginatorButtonsRow()
		val initMessage = paginator.initPaginator()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(initMessage)
			.addActionRows(row)
			.build()

		response.complete(commandResponse)
	}

	/**
	 * Creates a list of embed messages containing available bot commands and their descriptions. The commands are split
	 * into chunks, and each chunk is displayed in a separate embed.
	 *
	 * @param context The context of the command execution.
	 * @param commands A list of command details to be included in the embed.
	 * @return A list of MessageEmbed objects, each containing a chunk of commands with descriptions.
	 */
	private fun createHelpComponents(
		context: CommandBaseContext,
		commands: List<Command>,
	): List<MessageEmbed> {
		val sortedCommands = commands.sorted()

		val website = environment.getProperty<String>(BotProperty.LINK_WEBSITE)
		val statusPage = environment.getProperty<String>(BotProperty.LINK_STATUS)
		val repository = environment.getProperty<String>(BotProperty.LINK_REPOSITORY)
		val docsLink = createLinkFromFragment(BotProperty.LINK_FRAGMENT_DOCS)

		val parsedCommands = parseCommandsWithDescription(context, sortedCommands)
		val lang = context.language

		val descriptionElements = listOf(
			mdBold(i18n.t(I18nResponseSource.HELPFUL_LINKS, lang).uppercase(Locale.getDefault())),
			mdLink(i18n.t(I18nResponseSource.BOT_WEBSITE, lang), website),
			mdLink(i18n.t(I18nResponseSource.INFRA_CURRENT_STATUS, lang), statusPage),
			mdLink(i18n.t(I18nResponseSource.BOT_SOURCE_CODE, lang), repository),
			mdLink(i18n.t(I18nResponseSource.BOT_DOCUMENTATION, lang), docsLink),
			"",
			mdBold("${i18n.t(I18nResponseSource.COMMANDS, lang).uppercase(Locale.getDefault())} (${commands.size})"),
		)
		return createEmbedMessages(context, parsedCommands, descriptionElements)
	}

	/**
	 * Parses the given commands and generates a map where keys represent the formatted command names, and values contain
	 * their descriptions.
	 *
	 * @param context The context of the command execution.
	 * @param guildCommands A sorted list of command details to be included in the embed.
	 * @return A map with command names as keys and descriptions as values.
	 */
	private fun parseCommandsWithDescription(
		context: CommandBaseContext,
		guildCommands: List<Command>
	): Map<String, String> {
		val commands = mutableMapOf<String, String>()
		val lang = context.language
		for (details in guildCommands) {
			var commandName = details.textKey
			if (context.isSlashEvent) {
				commandName = commandName.replace(".", " ")
			}
			val commandLink = createLinkFromFragment(BotProperty.LINK_FRAGMENT_COMMAND, details.textKey.replace(".", "-"))
			val keyJoiner = StringJoiner("")
			val descriptionJoiner = StringJoiner("")

			keyJoiner.add(context.prefix)
			keyJoiner.add("$commandName ")

			details.argumentsDefinition?.let { keyJoiner.add(mdCode("<${i18n.t(it, lang)}>")) }
			descriptionJoiner.add(mdLink("[link]", commandLink))
			descriptionJoiner.add(" ")
			descriptionJoiner.add(i18n.t(details, lang))

			commands[keyJoiner.toString()] = descriptionJoiner.toString()
		}
		return commands
	}

	/**
	 * Creates a list of embed messages that display a chunk of commands and their descriptions. The descriptionElements
	 * parameter allows additional text or links to be included in the first embed.
	 *
	 * @param context The context of the command execution.
	 * @param commands A map of command names and descriptions to be included in the embed.
	 * @param descriptionElements A list of additional elements (ex. links) to be displayed in the first embed.
	 * @return A list of MessageEmbed objects, each containing a chunk of commands with descriptions.
	 */
	private fun createEmbedMessages(
		context: CommandBaseContext,
		commands: Map<String, String>,
		descriptionElements: List<String>,
	): List<MessageEmbed> {
		val lang = context.language
		val paginatorChunkSize = environment.getProperty<Int>(BotProperty.JDA_PAGINATION_CHUNK_SIZE)
		val listOfChunkedCommands = commands.entries.chunked(paginatorChunkSize).map { chunk ->
			chunk.associate { it.toPair() }
		}
		val messages = mutableListOf<MessageEmbed>()

		for (chunk in listOfChunkedCommands) {
			val messageBuilder = createEmbedMessage(context)
				.setTitle(i18n.t(I18nResponseSource.HELP, lang))
				.setDescription(descriptionElements.joinToString("\n"))

			for ((commandKey, commandDescription) in chunk) {
				messageBuilder.setKeyValueField(commandKey, commandDescription, inline = false)
			}
			val message = messageBuilder
				.setColor(JdaColor.PRIMARY)
				.build()
			messages.add(message)
		}
		return messages
	}
}
