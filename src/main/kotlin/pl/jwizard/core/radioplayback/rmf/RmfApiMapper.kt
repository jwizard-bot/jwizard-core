/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback.rmf

enum class RmfApiMapper(
	private val stationSlug: String,
	private val endpointName: String,
	private val providerUrl: String,
) {
	RMF_FM("rmf-fm", "playlista_5.json.txt", "www.rmf.fm"),
	RMF_MAXX("rmf-maxx", "playlista_213.json.txt", "www.rmf.fm"),
	;

	companion object {
		private const val API_URL = "https://www.rmfon.pl/stacje"

		fun getTypeForSlug(stationSlug: String): RmfApiMapper? = entries.find { it.stationSlug == stationSlug }
	}

	fun parseToUrl(): String = "$API_URL/${endpointName}"

	fun getProvider(): String = providerUrl
}
