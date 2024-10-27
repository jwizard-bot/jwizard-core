/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql.bind

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.spi.RemotePropertySupplier
import pl.jwizard.jwc.persistence.sql.JdbcKtTemplateBean
import kotlin.reflect.KClass

/**
 * Implementation of the [RemotePropertySupplier] interface that provides access to remote properties
 * from a database using JDBC.
 *
 * This class interacts with a database to fetch global properties and specific properties for a given guild.
 *
 * @property jdbcKtTemplateBean The bean responsible for JDBC operations.
 * @author Miłosz Gilga
 */
@Component
class RemotePropertySupplierBean(private val jdbcKtTemplateBean: JdbcKtTemplateBean) : RemotePropertySupplier {

	/**
	 * Retrieves a specific property for a given guild from the database.
	 *
	 * This method constructs a SQL query to fetch a property value based on the provided column name
	 * and guild ID. The query is executed, and the result is cast to the specified type [T].
	 *
	 * @param T The type to which the property value should be cast.
	 * @param columnName The name of the column to fetch from the `guilds` table.
	 * @param guildId The ID of the guild for which the property is fetched.
	 * @param type The [KClass] representing the type to which the property value should be cast.
	 * @return The property value cast to type [T], or null if no value is found.
	 */
	override fun <T : Any> getProperty(columnName: String, guildId: Long, type: KClass<T>): T? {
		val sql = jdbcKtTemplateBean.parse(
			"SELECT {{columnName}} FROM guilds WHERE discord_id = ?",
			mapOf("columnName" to columnName)
		)
		return jdbcKtTemplateBean.queryForNullableObject(sql, type, guildId)
	}

	/**
	 * Retrieves multiple properties for a given guild from the database.
	 *
	 * This method constructs a SQL query to fetch multiple property values for a guild based on the provided list of
	 * column names. It then returns a map where the key is the column name, and the value is the corresponding property
	 * value.
	 *
	 * @param columnNames A list of column names whose values should be fetched from the `guilds` table.
	 * @param guildId The ID of the guild for which the properties are fetched.
	 * @return A map containing the column names as keys and their corresponding values as map values.
	 */
	override fun getCombinedProperties(columnNames: List<String>, guildId: Long): Map<String, Any?> {
		val sql = jdbcKtTemplateBean.parse(
			"SELECT {{columnNames}} FROM guilds WHERE discord_id = ?",
			mapOf("columnNames" to columnNames.joinToString(","))
		)
		return jdbcKtTemplateBean.queryForMap(sql, guildId)
	}
}
