/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.misc

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.refer.CommandArgument
import pl.jwizard.jwc.command.reflect.CommandDetails
import pl.jwizard.jwc.command.reflect.JdaCommand
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
 * Command class responsible for handling the `Help` command. This command provides a list of available commands in
 * the bot and helps the user navigate through them.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.HELP)
class HelpCmd(commandEnvironment: CommandEnvironmentBean) : CommandBase(commandEnvironment) {

	/**
	 * Retrieves and returns a map of commands that are enabled for the current guild.
	 *
	 * @param context The context of the command execution, containing guild, user, and event information.
	 * @param response The future response object used to send back the command output to Discord.
	 */
	override fun execute(context: CommandContext, response: TFutureResponse) {
		val enabledCommands = commandDataSupplier.getEnabledGuildCommandKeys(context.guildDbId, context.isSlashEvent)
		val guildCommands = commandsCacheBean.commands.filter { it.key in enabledCommands }

		val paginator = createPaginator(context, createHelpComponents(context, guildCommands))
		val row = paginator.createPaginatorButtonsRow()
		val initMessage = paginator.initPaginator()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(initMessage)
			.addActionRows(row)
			.build()

		response.complete(commandResponse)
	}

	/**
	 * Checks whether the command should be executed in private mode. If the command is executed with a "private"
	 * argument set to true, it returns the author's ID to restrict visibility.
	 *
	 * @param context The context of the command execution.
	 * @return The author's ID if the command is private, or null if it is not private.
	 */
	override fun isPrivate(context: CommandContext): Long? {
		val isPrivate = context.getNullableArg<Boolean>(CommandArgument.PRIVATE)
		return if (isPrivate == true) context.author.idLong else null
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
			mdBold("${i18nBean.t(I18nResponseSource.COMMANDS, lang).uppercase(Locale.getDefault())} (${commands.size})"),
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
	 * @param descriptionElements A list of additional elements (ex. links) to be displayed in the first embed.
	 * @return A list of MessageEmbed objects, each containing a chunk of commands with descriptions.
	 */
	private fun createEmbedMessages(
		context: CommandContext,
		commands: Map<String, String>,
		descriptionElements: List<String>,
	): List<MessageEmbed> {
		val lang = context.guildLanguage
		val paginatorChunkSize = environmentBean.getProperty<Int>(BotProperty.JDA_PAGINATION_CHUNK_SIZE)
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
}
