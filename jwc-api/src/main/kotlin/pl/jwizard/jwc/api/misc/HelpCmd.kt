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

@JdaCommand(Command.HELP)
internal class HelpCmd(
	private val commandDataSupplier: CommandDataSupplier,
	private val moduleDataSupplier: ModuleDataSupplier,
	commandEnvironment: CommandEnvironmentBean,
) : CommandBase(commandEnvironment), GlobalCommandHandler {
	override fun execute(context: GuildCommandContext, response: TFutureResponse) {
		val disabledCommands = commandDataSupplier
			.getDisabledGuildCommands(context.guildDbId, context.isSlashEvent)
		val disabledModules = moduleDataSupplier.getDisabledGuildModules(context.guildDbId)

		// get only enabled guild commands based disabled commands and modules
		val guildCommands = Command.entries.filter {
			it.dbId !in disabledCommands && it.module.dbId !in disabledModules
		}
		executeCommand(guildCommands, context, response)
	}

	// run event, when user executed this command as global Discord interaction
	override fun executeGlobal(context: GlobalCommandContext, response: TFutureResponse) =
		executeCommand(Command.entries, context, response)

	override fun isPrivate(context: GuildCommandContext): Long? {
		val isPrivate = context.getNullableArg<Boolean>(Argument.PRIVATE)
		return if (isPrivate == true) context.author.idLong else null
	}

	private fun executeCommand(
		commands: List<Command>,
		context: CommandBaseContext,
		response: TFutureResponse
	) {
		val paginator = createPaginator(context, createHelpComponents(context, commands))
		val initMessage = paginator.initPaginator()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(initMessage)
			.addActionRows(paginator.paginatorButtonsRow)
			.build()

		response.complete(commandResponse)
	}

	private fun createHelpComponents(
		context: CommandBaseContext,
		commands: List<Command>,
	): List<MessageEmbed> {
		val lang = context.language
		val paginatorChunkSize = environment.getProperty<Int>(BotProperty.JDA_PAGINATION_CHUNK_SIZE)

		val website = environment.getProperty<String>(BotProperty.LINK_WEBSITE)
		val statusPage = environment.getProperty<String>(BotProperty.LINK_STATUS)
		val repository = environment.getProperty<String>(BotProperty.LINK_REPOSITORY)

		val parsedCommands = mutableMapOf<String, String>()
		// sort commands alphabetically
		for (details in commands.sorted()) {
			val commandLink = createLinkFromFragment(BotProperty.LINK_FRAGMENT_COMMAND, details.toUrl)
			val keyJoiner = StringJoiner("")
			val descriptionJoiner = StringJoiner("")

			keyJoiner.add(details.parseWithPrefix(context))
			details.argumentsDefinition?.let { keyJoiner.add(mdCode("<${i18n.t(it, lang)}>")) }

			descriptionJoiner.add(mdLink("[link]", commandLink))
			descriptionJoiner.add(" ")
			descriptionJoiner.add(i18n.t(details, lang))

			parsedCommands[keyJoiner.toString()] = descriptionJoiner.toString()
		}

		val descriptionElements = listOf(
			mdBold(i18n.t(I18nResponseSource.HELPFUL_LINKS, lang).uppercase(Locale.getDefault())),
			mdLink(i18n.t(I18nResponseSource.BOT_WEBSITE, lang), website),
			mdLink(i18n.t(I18nResponseSource.INFRA_CURRENT_STATUS, lang), statusPage),
			mdLink(i18n.t(I18nResponseSource.BOT_SOURCE_CODE, lang), repository),
			"",
			mdBold(
				"${
					i18n.t(I18nResponseSource.COMMANDS, lang).uppercase(Locale.getDefault())
				} (${commands.size})"
			),
		)

		val listOfChunkedCommands = parsedCommands.entries.chunked(paginatorChunkSize).map { chunk ->
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
