package pl.jwizard.jwc.api.manager

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.ManagerCommandBase
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nSystemSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.i18n.I18nLocaleSource

@JdaCommand(Command.SETTINGS)
class SettingsCmd(
	commandEnvironment: CommandEnvironmentBean
) : ManagerCommandBase(commandEnvironment) {
	override fun executeManager(context: GuildCommandContext, response: TFutureResponse) {
		val paginatorChunkSize = environment.getProperty<Int>(BotProperty.JDA_PAGINATION_CHUNK_SIZE)

		val properties = environment.getGuildMultipleProperties(
			// get guild properties only with i18n key and value converter
			guildProperties = GuildProperty.entries.filter {
				it.placeholder.isNotEmpty() && it.converter != null
			},
			guildId = context.guild.idLong,
		)
		val embedMessages = mutableListOf<MessageEmbed>()

		// sort by guild properties enum order
		val sortedProps = properties.toList().sortedBy { it.first.ordinal }

		for (chunk in sortedProps.chunked(paginatorChunkSize)) {
			val messageBuilder = createEmbedMessage(context)
				.setTitle(I18nSystemSource.GUILD_SETTINGS_HEADER)

			for ((index, pair) in chunk.withIndex()) {
				val (column, value) = pair
				val converter = column.converter
				// skip values, where not have i18n key or value converter
				if (column.placeholder.isEmpty() || converter == null) {
					continue
				}
				val key = i18n.t(column, context.language)
				val convertedValue = converter.mapper(value)
				val parsedValue = if (converter.isI18nContent) {
					i18n.t(convertedValue as I18nLocaleSource, context.language)
				} else {
					convertedValue.toString()
				}
				messageBuilder.setKeyValueField(key, parsedValue)
				// add blank space after every odd element
				if (index % 2 == 0) {
					messageBuilder.setSpace()
				}
			}
			val message = messageBuilder
				.setColor(JdaColor.PRIMARY)
				.build()
			embedMessages.add(message)
		}
		val paginator = createPaginator(context, embedMessages)
		val initMessage = paginator.initPaginator()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(initMessage)
			.addActionRows(paginator.paginatorButtonsRow)
			.build()

		response.complete(commandResponse)
	}

	override fun isPrivate(context: GuildCommandContext): Long? {
		val isPrivate = context.getNullableArg<Boolean>(Argument.PRIVATE)
		return if (isPrivate == true) context.author.idLong else null
	}
}
