/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.radio

import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nDynamicMod
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.util.mdLink

/**
 * Command that displays available radio stations for the guild.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.SHOW_RADIOS)
class ShowRadioStationsCmd(commandEnvironment: CommandEnvironmentBean) : CommandBase(commandEnvironment) {

	/**
	 * Executes the command to display radio stations.
	 *
	 * This method retrieves radio stations for the specified guild, constructs embed messages for each page of radio
	 * stations, and handles pagination. If there are no radio stations available, a message indicating this is returned.
	 *
	 * @param context The context of the command execution, containing user interaction details.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun execute(context: CommandContext, response: TFutureResponse) {
		val radioStations = radioStationSupplier.getRadioStations(context.guildDbId)
		val lang = context.guildLanguage

		val paginatorChunkSize = environmentBean.getProperty<Int>(BotProperty.JDA_PAGINATION_CHUNK_SIZE)
		val radioCmd = Command.PLAY_RADIO.propName
		val responseBuilder = CommandResponse.Builder()

		val message = if (radioStations.isNotEmpty()) {
			val radioStationsPages = radioStations.entries
				.map {
					val name = i18nBean.tRaw(I18nDynamicMod.ARG_OPTION_MOD, arrayOf(radioCmd, it.key), lang)
					val link = mdLink("[${i18nBean.t(I18nUtilSource.WEBSITE, lang)}]", it.value)
					"* $name $link"
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

			responseBuilder.addActionRows(paginator.createPaginatorButtonsRow())
			firstMessage
		} else {
			createEmbedMessage(context)
				.setDescription(I18nResponseSource.NO_RADIO_STATION_INFO)
				.setColor(JdaColor.PRIMARY)
				.build()
		}
		responseBuilder.addEmbedMessages(message)
		response.complete(responseBuilder.build())
	}
}
