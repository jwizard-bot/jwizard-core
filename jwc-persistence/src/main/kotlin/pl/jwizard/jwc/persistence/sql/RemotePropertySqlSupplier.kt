package pl.jwizard.jwc.persistence.sql

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.spi.RemotePropertySupplier
import pl.jwizard.jwl.persistence.sql.JdbiQuery
import kotlin.reflect.KClass

@Component
internal class RemotePropertySqlSupplier(
	private val jdbiQuery: JdbiQuery,
) : RemotePropertySupplier {
	override fun <T : Any> getProperty(columnName: String, guildId: Long, type: KClass<T>): T? {
		val sql = jdbiQuery.parse(
			"SELECT {{columnName}} FROM guilds WHERE discord_id = ?",
			mapOf("columnName" to columnName)
		)
		return jdbiQuery.queryForNullableObject(sql, type, guildId)
	}

	override fun getCombinedProperties(columnNames: List<String>, guildId: Long): Map<String, Any?> {
		val sql = jdbiQuery.parse(
			"SELECT {{columnNames}} FROM guilds WHERE discord_id = ?",
			mapOf("columnNames" to columnNames.joinToString(","))
		)
		return jdbiQuery.queryForSingleMap(sql, guildId)
	}
}
