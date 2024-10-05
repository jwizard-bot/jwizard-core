/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio.spi

import pl.jwizard.jwc.core.radio.RadioStationDetails
import java.math.BigInteger

/**
 * Defines methods for supplying radio station data.
 *
 * This interface provides methods for retrieving radio stations from a data source. Implementations should define
 * how radio station data is fetched and managed.
 *
 * @author Miłosz Gilga
 */
interface RadioStationSupplier {

	/**
	 * Retrieves a map of radio stations.
	 *
	 * This method should return a map where each entry consists of a radio station slug as the key and its name as
	 * the value.
	 *
	 * @param guildDbId The ID of the guild to filter the radio stations.
	 * @return A map of radio stations with slugs as keys and names as values.
	 */
	fun getRadioStations(guildDbId: BigInteger): Map<String, String>

	/**
	 * Retrieves a specific radio station based on its slug and guild ID.
	 *
	 * This method should return a [RadioStationDetails] instance if found, or null if no matching radio station exists
	 * for the given slug and guild ID.
	 *
	 * @param slug The unique slug of the radio station to retrieve.
	 * @param guildDbId The ID of the guild to filter the radio stations.
	 * @return The [RadioStationDetails] instance if found, or null if not found.
	 */
	fun getRadioStation(slug: String, guildDbId: BigInteger): RadioStationDetails?
}
