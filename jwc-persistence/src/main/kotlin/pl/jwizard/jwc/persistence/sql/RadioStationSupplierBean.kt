/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.radio.RadioStationDetails
import pl.jwizard.jwc.radio.spi.RadioStationSupplier
import pl.jwizard.jwl.persistence.sql.ColumnDef
import pl.jwizard.jwl.persistence.sql.JdbcKtTemplateBean
import java.math.BigInteger

/**
 * Implementation of [RadioStationSupplier] that retrieves radio station data from a database.
 *
 * This component uses a [JdbcKtTemplateBean] to execute SQL queries and provide implementations for the methods
 * defined in [RadioStationSupplier]. It interacts with the database to fetch radio station details.
 *
 * @property jdbcKtTemplateBean The [JdbcKtTemplateBean] used for database interactions.
 * @author Miłosz Gilga
 */
@Component
class RadioStationSupplierBean(private val jdbcKtTemplateBean: JdbcKtTemplateBean) : RadioStationSupplier {

	/**
	 * Retrieves a map of radio stations from the database. This method executes a SQL query to fetch radio station
	 * slugs, and returns them as a list.
	 *
	 * @param guildDbId The ID of the guild to filter the radio stations.
	 * @return A list of radio stations infos (name as key and website as value).
	 */
	override fun getRadioStations(guildDbId: BigInteger): Map<String, String> {
		val sql = """
			SELECT name, webpage_url webpageUrl FROM radios r
			LEFT JOIN guilds_disabled_radios gdr ON gdr.radio_id = r.id AND gdr.guild_id = ?
			WHERE gdr.guild_id IS NULL
		"""
		return jdbcKtTemplateBean.queryForListMap(
			sql,
			key = ColumnDef("name", String::class),
			value = ColumnDef("webpageUrl", String::class),
			guildDbId
		)
	}

	/**
	 * Retrieves a specific radio station from the database based on its slug and guild ID.
	 *
	 * This method executes a SQL query to fetch the details of a radio station by its slug and guild ID. It returns
	 * a [RadioStationDetails] instance if found, or null if no matching record exists.
	 *
	 * @param slug The unique slug of the radio station to retrieve.
	 * @param guildDbId The ID of the guild to filter the radio stations.
	 * @return The [RadioStationDetails] instance if found, or null if not found.
	 */
	override fun getRadioStation(slug: String, guildDbId: BigInteger): RadioStationDetails? {
		val sql = """
			SELECT name, stream_url, CONCAT(rpa.url, '/', playback_api_suffix) playbackApiUrl, parser_class_name
			FROM radios r
			INNER JOIN radio_playback_apis rpa ON r.playback_api_id = rpa.id
			LEFT JOIN guilds_disabled_radios gdr ON gdr.radio_id = r.id AND gdr.guild_id = ?
			WHERE name = ? AND gdr.guild_id IS NULL
		"""
		return jdbcKtTemplateBean.queryForDataClass(sql, RadioStationDetails::class, guildDbId, slug)
	}
}
