/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * A custom extension of [JdbcTemplate] that provides additional utility methods for querying and parsing.
 *
 * This class extends [JdbcTemplate] to add custom methods for handling SQL queries and processing results. It
 * includes functionality for safely querying for objects that might not exist, parsing SQL queries with placeholders,
 * and converting values to long integers.
 *
 * @property datasource The data source used by this template to interact with the database.
 * @author Miłosz Gilga
 */
class JdbcKtTemplateBean(private val datasource: DataSource) : JdbcTemplate(datasource) {

	companion object {
		/**
		 * The start delimiter used in query parsing.
		 */
		private const val START_DELIMITER = "{{"

		/**
		 * The stop delimiter used in query parsing.
		 */
		private const val STOP_DELIMITER = "}}"
	}

	/**
	 * Executes a query and retrieves a single result, which may be null.
	 *
	 * This method executes the provided SQL query and attempts to map the result to the specified type [T]. If no
	 * result is found (causing an [EmptyResultDataAccessException]), it returns null instead.
	 *
	 * @param T The type to which the result should be mapped.
	 * @param sql The SQL query to execute.
	 * @param type The [KClass] representing the target type for the result.
	 * @param args Optional arguments for the SQL query.
	 * @return The result of the query cast to type [T], or null if no result is found.
	 */
	fun <T : Any> queryForNullableObject(sql: String, type: KClass<T>, vararg args: Any) = try {
		super.queryForObject(sql, type.java, *args)
	} catch (_: EmptyResultDataAccessException) {
		null
	}

	/**
	 * Executes a SQL query and returns the results as a map where each entry's key and value are mapped from the
	 * specified columns.
	 *
	 * @param U The type of the key column.
	 * @param V The type of the value column.
	 * @param sql The SQL query to execute.
	 * @param key Definition of the key column, including its type and column name.
	 * @param value Definition of the value column, including its type and column name.
	 * @param args Optional arguments for the SQL query.
	 * @return A map where each key-value pair is derived from the specified columns in the query result.
	 */
	fun <U : Any, V : Any> queryForListMap(sql: String, key: ColumnDef<U>, value: ColumnDef<V>, vararg args: Any) =
		query(sql.trimIndent(), { rs, _ ->
			key.type.cast(rs.getObject(key.columnName)) to value.type.cast(rs.getObject(value.columnName))
		}, *args).toMap()

	/**
	 * Replaces placeholders in the input string with the provided replacement values.
	 *
	 * This method processes the input string, replacing occurrences of placeholders in the format `{{key}}` with
	 * corresponding values from the [replacements] map.
	 *
	 * @param input The string containing placeholders to replace.
	 * @param replacements A map of placeholders and their replacement values.
	 * @return The resulting string with placeholders replaced by actual values, or an empty string if input is null.
	 */
	fun parse(input: String, replacements: Map<String, Any>): String {
		var result = input
		for ((key, value) in replacements) {
			result = result.replace("$START_DELIMITER$key$STOP_DELIMITER", value.toString())
		}
		return result
	}

	/**
	 * Inserts multiple records into the specified table.
	 *
	 * This method constructs an SQL `INSERT` statement based on the provided table name and column definitions,
	 * executes the statement.
	 *
	 * @param tableName The name of the table into which records are inserted.
	 * @param columns A map of column names to [SqlColumn] definitions, where each column contains its value and type.
	 */
	fun insertMultiples(tableName: String, columns: Map<String, SqlColumn>) {
		val sql = parse(
			input = "INSERT INTO {{table}}({{columns}}) VALUES ({{questionMarks}})",
			replacements = mapOf(
				"table" to tableName,
				"columns" to columns.keys.joinToString(separator = ","),
				"questionMarks" to List(columns.size) { "?" }.joinToString(separator = ","),
			)
		)
		update {
			val ps = it.prepareStatement(sql)
			columns.values.forEachIndexed { index, column -> ps.setObject(index + 1, column.value, column.type) }
			ps
		}
	}

	/**
	 * Executes a query and retrieves a single result as a data class.
	 *
	 * This method executes the provided SQL query and attempts to map the result to the specified data class type [T].
	 * It returns the first result or null if no result is found.
	 *
	 * @param T The type of the data class to which the result should be mapped.
	 * @param sql The SQL query to execute.
	 * @param type The [KClass] representing the data class type for the result.
	 * @param args Optional arguments for the SQL query.
	 * @return The result of the query mapped to type [T], or null if no result is found.
	 */
	fun <T : Any> queryForDataClass(sql: String, type: KClass<T>, vararg args: Any): T? =
		super.query(sql.trimIndent(), DataClassRowMapper(type.java), *args).firstOrNull()

	/**
	 * Executes a query and retrieves a single result, which may be null.
	 *
	 * This method executes the provided SQL query and attempts to map the result to the specified type [T]. If no
	 * result is found, it returns null.
	 *
	 * @param T The type to which the result should be mapped.
	 * @param sql The SQL query to execute.
	 * @param type The [KClass] representing the target type for the result.
	 * @param args Optional arguments for the SQL query.
	 * @return The result of the query cast to type [T], or null if no result is found.
	 */
	fun <T : Any> queryForObject(sql: String, type: KClass<T>, vararg args: Any): T? =
		super.queryForObject(sql.trimIndent(), type.java, *args)

	/**
	 * Executes a query and retrieves a list of results.
	 *
	 * This method executes the provided SQL query and retrieves a list of results mapped to the specified type [T].
	 * If no results are found, it returns an empty list.
	 *
	 * @param T The type to which the results should be mapped.
	 * @param sql The SQL query to execute.
	 * @param type The [KClass] representing the target type for the results.
	 * @param args Optional arguments for the SQL query.
	 * @return A list of results cast to type [T], or an empty list if no results are found.
	 */
	fun <T : Any> queryForList(sql: String, type: KClass<T>, vararg args: Any): List<T> =
		super.queryForList(sql.trimIndent(), type.java, *args) ?: emptyList()

	/**
	 * Executes a query and retrieves a single boolean result, defaulting to false if no result is found.
	 *
	 * This method executes the provided SQL query and attempts to map the result to a boolean value. If no result is
	 * found, it defaults to returning false.
	 *
	 * @param sql The SQL query to execute.
	 * @param args Optional arguments for the SQL query.
	 * @return The result of the query as a boolean value, or false if no result is found.
	 */
	fun queryForBool(sql: String, vararg args: Any) =
		queryForNullableObject(sql.trimIndent(), Boolean::class, *args) ?: false
}
