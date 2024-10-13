/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.CommandDetails
import pl.jwizard.jwc.core.i18n.source.I18nDynamicMod
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.util.mdBold
import pl.jwizard.jwc.core.util.mdCode
import pl.jwizard.jwc.core.util.mdLink
import java.util.*

/**
 * Abstract base class for handling bot help commands, providing common functionality for displaying available commands
 * in an organized and paginated format.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
abstract class HelpCommandBase(commandEnvironment: CommandEnvironmentBean) : CommandBase(commandEnvironment) {

	/**
	 * The size of chunks used to split commands when creating embed messages. Each chunk will contain up to 8 commands
	 * to be displayed in one embed message.
	 */
	private val paginatorChunkSize = environmentBean.getProperty<Int>(BotProperty.JDA_PAGINATION_CHUNK_SIZE)

	/**
	 * Executes the help command, generating and displaying paginated help information for all available commands.
	 *
	 * @param context The context of the command execution, containing guild, user, and other relevant data.
	 * @param response The future response object used to send back the command output to Discord.
	 */
	final override fun execute(context: CommandContext, response: TFutureResponse) {
		val commands = executeHelp(context, response)

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
	 * @param context The context of the command execution, containing guild and user data.
	 * @param commands A map of command names and their details to be included in the embed.
	 * @return A list of MessageEmbed objects, each containing a chunk of commands with descriptions.
	 */
	private fun createHelpComponents(
		context: CommandContext,
		commands: Map<String, CommandDetails>,
	): List<MessageEmbed> {
		val sortedCommands = commands.toSortedMap()

		val website = environmentBean.getProperty<String>(BotProperty.LINK_WEBSITE)
		val repository = environmentBean.getProperty<String>(BotProperty.LINK_REPOSITORY)
		val docs = environmentBean.getProperty<String>(BotProperty.LINK_DOCS)

		val parsedCommands = parseCommandsWithDescription(context, sortedCommands, website)
		val lang = context.guildLanguage

		val descriptionElements = listOf(
			mdBold(i18nBean.t(I18nResponseSource.HELPFUL_LINKS, lang).uppercase(Locale.getDefault())),
			mdLink(i18nBean.t(I18nResponseSource.BOT_WEBSITE, lang), website),
			mdLink(i18nBean.t(I18nResponseSource.BOT_SOURCE_CODE, lang), repository),
			mdLink(i18nBean.t(I18nResponseSource.BOT_DOCUMENTATION, lang), docs.format(website)),
			"",
			mdBold(i18nBean.t(I18nResponseSource.COMMANDS, lang).uppercase(Locale.getDefault())),
		)
		return createEmbedMessages(context, parsedCommands, descriptionElements)
	}

	/**
	 * Parses the given commands and generates a map where keys represent the formatted command names, and values contain
	 * their descriptions.
	 *
	 * @param context The context of the command execution, containing guild and user data.
	 * @param guildCommands A sorted map of command names and their details.
	 * @param website The URL of the bot's website used for linking in descriptions.
	 * @return A map with command names as keys and descriptions as values.
	 */
	private fun parseCommandsWithDescription(
		context: CommandContext,
		guildCommands: SortedMap<String, CommandDetails>,
		website: String,
	): Map<String, String> {
		val commands = mutableMapOf<String, String>()
		val lang = context.guildLanguage

		val command = environmentBean.getProperty<String>(BotProperty.LINK_COMMAND)

		for ((key, details) in guildCommands) {
			val keyJoiner = StringJoiner("")
			val descriptionJoiner = StringJoiner("")

			keyJoiner.add(context.prefix)
			keyJoiner.add(key)
			keyJoiner.add(" (${details.alias}) ")

			details.argI18nKey?.let {
				keyJoiner.add(mdCode("<${i18nBean.tRaw(I18nDynamicMod.ARG_PER_COMMAND_MOD, arrayOf(it), lang)}>"))
			}
			descriptionJoiner.add(mdLink("[link]", command.format(website, key)))
			descriptionJoiner.add(" ")
			descriptionJoiner.add(i18nBean.tRaw(I18nDynamicMod.COMMANDS_MOD, arrayOf(key), lang))

			commands[keyJoiner.toString()] = descriptionJoiner.toString()
		}
		return commands
	}

	/**
	 * Creates a list of embed messages that display a chunk of commands and their descriptions. The descriptionElements
	 * parameter allows additional text or links to be included in the first embed.
	 *
	 * @param context The context of the command execution, containing guild and user data.
	 * @param commands A map of command names and descriptions to be included in the embed.
	 * @param descriptionElements A list of additional elements (e.g., links) to be displayed in the first embed.
	 * @return A list of MessageEmbed objects, each containing a chunk of commands with descriptions.
	 */
	private fun createEmbedMessages(
		context: CommandContext,
		commands: Map<String, String>,
		descriptionElements: List<String>,
	): List<MessageEmbed> {
		val lang = context.guildLanguage
		val listOfChunkedCommands = commands.entries.chunked(paginatorChunkSize).map { chunk ->
			chunk.associate { it.toPair() }
		}
		val messages = mutableListOf<MessageEmbed>()

		for (chunk in listOfChunkedCommands) {
			val messageBuilder = createEmbedMessage(context)
				.setTitle(i18nBean.t(I18nResponseSource.HELP, lang))
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

	/**
	 * Abstract method that must be implemented to provide the help information for commands. It should return a map of
	 * command names and their details, which will be used to build the help message.
	 *
	 * @param context The context of the command execution, containing guild, user, and other relevant data.
	 * @param response The future response object used to send back the command output to Discord.
	 * @return A map of command names and their corresponding CommandDetails.
	 */
	protected abstract fun executeHelp(context: CommandContext, response: TFutureResponse): Map<String, CommandDetails>
}
