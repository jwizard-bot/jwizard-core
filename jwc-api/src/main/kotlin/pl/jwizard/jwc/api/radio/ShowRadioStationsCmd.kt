package pl.jwizard.jwc.api.radio

import pl.jwizard.jwc.api.CommandBase
import pl.jwizard.jwc.api.CommandEnvironment
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwc.core.util.mdList
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.radio.RadioStation

@JdaCommand(Command.RADIO_ALL)
internal class ShowRadioStationsCmd(
	commandEnvironment: CommandEnvironment,
) : CommandBase(commandEnvironment) {
	override fun execute(context: GuildCommandContext, response: TFutureResponse) {
		val radioStations = RadioStation.entries
		val lang = context.language

		val paginatorChunkSize = environment.getProperty<Int>(BotProperty.JDA_PAGINATION_CHUNK_SIZE)
		val responseBuilder = CommandResponse.Builder()

		// if is any declared radio station
		val message = if (radioStations.isNotEmpty()) {
			val radioStationsPages = radioStations
				.map {
					val link = mdLink("[${i18n.t(I18nUtilSource.WEBSITE, lang)}]", it.website)
					mdList("${i18n.t(it, lang)} $link")
				}
				.chunked(paginatorChunkSize)

			val pages = radioStationsPages.map {
				createEmbedMessage(context)
					.setTitle(I18nResponseSource.RADIO_STATIONS_INFO)
					.setDescription(it.joinToString("\n"))
					.setColor(JdaColor.PRIMARY)
					.build()
			}
			val paginator = createPaginator(context, pages)
			val firstMessage = paginator.initPaginator()

			responseBuilder.addActionRows(paginator.paginatorButtonsRow)
			firstMessage
		} else {
			// otherwise return single embed message with message info
			createEmbedMessage(context)
				.setDescription(I18nResponseSource.NO_RADIO_STATION_INFO)
				.setColor(JdaColor.PRIMARY)
				.build()
		}
		responseBuilder.addEmbedMessages(message)
		response.complete(responseBuilder.build())
	}
}
