/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.radio

import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale

@CommandListenerBean(id = BotCommand.SHOW_RADIOS)
class ShowRadioStationsCmd(
	botConfiguration: BotConfiguration,
) : AbstractCompositeCmd(
	botConfiguration
) {
	override fun execute(event: CompoundCommandEvent) {
		// fetch all radio stations for selected guild (some of the stations can be turned off)
		val radioStations = radioSupplier.fetchRadioStations(event.guildDbId)
		var i18nLocale = I18nResLocale.NO_RADIO_STATION_INFO
		var callbackPaginator: () -> Unit = {}

		// for existing radio stations
		if (radioStations.isNotEmpty()) {
			i18nLocale = I18nResLocale.RADIO_STATIONS_INFO

			val parsedRadioStations = radioStations.map { "* ${it.name} - `${it.slug}`" }
			val paginator = createDefaultPaginator(parsedRadioStations, 15)

			callbackPaginator = { paginator.display(event.textChannel) }
		}
		// for non existing or all turned off radio stations
		val embedMessage = CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(i18nLocale)
			.addColor(EmbedColor.WHITE)
			.build()

		event.appendEmbedMessage(embedMessage, callbackPaginator)
	}
}
