/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback

interface RadioStationPlaybackFetcher {
	fun fetchData(stationSlug: String): RadioPlaybackResponseData?
}
