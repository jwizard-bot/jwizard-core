/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.misc

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
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
) : CommandBase(commandEnvironment) {

	/**
	 * Retrieves and returns a map of commands that are enabled for the current guild.
	 *
	 * @param context The context of the command execution, containing guild, user, and event information.
	 * @param response The future response object used to send back the command output to Discord.
	 */
	override fun execute(context: CommandContext, response: TFutureResponse) {
		val disabledCommands = commandDataSupplier.getDisabledGuildCommands(context.guildDbId, context.isSlashEvent)
		val disabledModules = moduleDataSupplier.getDisabledGuildModules(context.guildDbId)

		val guildCommands = Command.entries.filter {
			it.dbId !in disabledCommands && it.module.dbId !in disabledModules
		}
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
		val isPrivate = context.getNullableArg<Boolean>(Argument.PRIVATE)
		return if (isPrivate == true) context.author.idLong else null
	}

	/**
	 * Creates a list of embed messages containing available bot commands and their descriptions. The commands are split
	 * into chunks, and each chunk is displayed in a separate embed.
	 *
	 * @param context The context of the command execution, containing guild and user data.
	 * @param commands A list of command details to be included in the embed.
	 * @return A list of MessageEmbed objects, each containing a chunk of commands with descriptions.
	 */
	private fun createHelpComponents(
		context: CommandContext,
		commands: List<Command>,
	): List<MessageEmbed> {
		val sortedCommands = commands.sorted()

		val website = environmentBean.getProperty<String>(BotProperty.LINK_WEBSITE)
		val statusPage = environmentBean.getProperty<String>(BotProperty.LINK_STATUS)
		val repository = environmentBean.getProperty<String>(BotProperty.LINK_REPOSITORY)
		val docsLink = createLinkFromFragment(BotProperty.LINK_FRAGMENT_DOCS)

		val parsedCommands = parseCommandsWithDescription(context, sortedCommands, website)
		val lang = context.guildLanguage

		val descriptionElements = listOf(
			mdBold(i18nBean.t(I18nResponseSource.HELPFUL_LINKS, lang).uppercase(Locale.getDefault())),
			mdLink(i18nBean.t(I18nResponseSource.BOT_WEBSITE, lang), website),
			mdLink(i18nBean.t(I18nResponseSource.INFRA_CURRENT_STATUS, lang), statusPage),
			mdLink(i18nBean.t(I18nResponseSource.BOT_SOURCE_CODE, lang), repository),
			mdLink(i18nBean.t(I18nResponseSource.BOT_DOCUMENTATION, lang), docsLink),
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
	 * @param guildCommands A sorted list of command details to be included in the embed.
	 * @param website The URL of the bot's website used for linking in descriptions.
	 * @return A map with command names as keys and descriptions as values.
	 */
	private fun parseCommandsWithDescription(
		context: CommandContext,
		guildCommands: List<Command>,
		website: String,
	): Map<String, String> {
		val commands = mutableMapOf<String, String>()
		val lang = context.guildLanguage

		val command = environmentBean.getProperty<String>(BotProperty.LINK_FRAGMENT_COMMAND)

		for (details in guildCommands) {
			val keyJoiner = StringJoiner("")
			val descriptionJoiner = StringJoiner("")

			keyJoiner.add(context.prefix)
			keyJoiner.add(details.textKey)
			keyJoiner.add(" (${details.alias}) ")

			details.argumentsDefinition?.let { keyJoiner.add(mdCode("<${i18nBean.t(it, lang)}>")) }
			descriptionJoiner.add(mdLink("[link]", command.format(website, details.textKey)))
			descriptionJoiner.add(" ")
			descriptionJoiner.add(i18nBean.t(details, lang))

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
