/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback.zet

enum class ZetGroupApiMapper(
	private val stationSlug: String,
	private val endpointName: String,
	private val providerUrl: String,
) {
	ZET("zet", "radiozet.json?callback=rdsData", "rds.eurozet.pl"),
	MELO("melo", "zetgold.json?callback=rdsData", "rds.eurozet.pl"),
	ANTY("anty", "antyradio.json?callback=rdsData", "rds.eurozet.pl"),
	;

	companion object {
		private const val API_URL = "https://rds.eurozet.pl/reader/var"

		fun getTypeForSlug(stationSlug: String): ZetGroupApiMapper? =
			ZetGroupApiMapper.entries.find { it.stationSlug == stationSlug }
	}

	fun parseToUrl(): String = "$API_URL/$endpointName"

	fun getProvider(): String = providerUrl
}
