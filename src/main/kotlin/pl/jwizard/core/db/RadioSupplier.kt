/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db

interface RadioSupplier {
	fun fetchRadioStation(stationSlug: String, guildId: Long): RadioStationDto?
	fun fetchRadioStations(guildId: Long): List<RadioStationInfoDto>
}
