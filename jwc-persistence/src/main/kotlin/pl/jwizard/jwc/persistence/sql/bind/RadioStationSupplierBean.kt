/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql.bind

import org.springframework.stereotype.Component
import pl.jwizard.jwc.persistence.sql.ColumnDef
import pl.jwizard.jwc.persistence.sql.JdbcKtTemplateBean
import pl.jwizard.jwc.radio.RadioStation
import pl.jwizard.jwc.radio.spi.RadioStationSupplier
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
	 * Retrieves a map of radio stations from the database.
	 *
	 * This method executes a SQL query to fetch radio station names and slugs, and returns them as a map where
	 * the slug is the key and the name are the value.
	 *
	 * @param guildDbId The ID of the guild to filter the radio stations.
	 * @return A map of radio stations with slugs as keys and names as values.
	 */
	override fun getRadioStations(guildDbId: BigInteger): Map<String, String> {
		val sql = """
			SELECT name, slug
			FROM guilds_radio_stations_binding rsb
			INNER JOIN radio_stations rs ON rsb.radio_station_id = rs.id
			WHERE guild_id = ?
		""".trimIndent()
		return jdbcKtTemplateBean.queryForListMap(
			sql,
			ColumnDef("slug", String::class),
			ColumnDef("name", String::class),
			guildDbId
		)
	}

	/**
	 * Retrieves a specific radio station from the database based on its slug and guild ID.
	 *
	 * This method executes a SQL query to fetch the details of a radio station by its slug and guild ID. It returns
	 * a [RadioStation] instance if found, or null if no matching record exists.
	 *
	 * @param slug The unique slug of the radio station to retrieve.
	 * @param guildDbId The ID of the guild to filter the radio stations.
	 * @return The [RadioStation] instance if found, or null if not found.
	 */
	override fun getRadioStation(slug: String, guildDbId: BigInteger): RadioStation? {
		val sql = """
			SELECT name, slug, stream_url, proxy_stream_url, cover_image
			FROM guilds_radio_stations_binding rsb
			INNER JOIN radio_stations rs rsb.radio_station_id = rs.id
			WHERE slug = ? AND guild_id = ?
		""".trimIndent()
		return jdbcKtTemplateBean.queryForDataClass(sql, RadioStation::class, slug, guildDbId)
	}
}
