/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db.sql

import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import pl.jwizard.core.db.RadioStationDto
import pl.jwizard.core.db.RadioStationInfoDto
import pl.jwizard.core.db.RadioSupplier
import pl.jwizard.core.log.AbstractLoggingBean

@Service
class RadioSqlSupplier(
	private val jdbcTemplate: JdbcTemplate
) : RadioSupplier, AbstractLoggingBean(RadioSqlSupplier::class) {

	override fun fetchRadioStation(stationSlug: String, guildId: Long): RadioStationDto? {
		val sql = """
			SELECT name, slug, stream_url, proxy_stream_url, cover_image
			FROM guilds_radio_stations_binding AS grsb
			INNER JOIN radio_stations AS rs ON grsb.radio_station_id = rs.id
			WHERE slug = ? AND guild_id = ?
		""".trimIndent()
		val result = jdbcTemplate.query(
			sql,
			DataClassRowMapper(RadioStationDto::class.java),
			stationSlug,
			guildId
		).firstOrNull()
		var logStatement = "Successfully fetched radio station: $stationSlug for guild with id: $guildId"
		if (result == null) {
			logStatement = "Radio station: $stationSlug is turned off for guild: $guildId or does not exist"
		}
		log.debug(logStatement)
		return result
	}

	override fun fetchRadioStations(guildId: Long): List<RadioStationInfoDto> {
		val sql = """
			SELECT name, slug FROM guilds_radio_stations_binding AS grsb
			INNER JOIN radio_stations AS rs ON grsb.radio_station_id = rs.id
			WHERE guild_id = ?
		""".trimIndent()
		val result = jdbcTemplate.query(
			sql,
			{ rs, _ -> RadioStationInfoDto(rs.getString("name"), rs.getString("slug")) },
			guildId
		)
		log.debug("Successfully found: {} radio stations for guild: {}", result.size, guildId)
		return result
	}
}
