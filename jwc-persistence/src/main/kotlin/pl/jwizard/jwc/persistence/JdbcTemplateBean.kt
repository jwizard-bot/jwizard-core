/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.math.BigInteger
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
class JdbcTemplateBean(private val datasource: DataSource) : JdbcTemplate(datasource) {

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
		queryForObject(sql, type.java, args)
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
	 * @return A map where each key-value pair is derived from the specified columns in the query result.
	 */
	fun <U : Any, V : Any> queryForListMap(sql: String, key: ColumnDef<U>, value: ColumnDef<V>) =
		query(sql, RowMapper { rs, _ ->
			key.type.cast(rs.getObject(key.columnName)) to value.type.cast(rs.getObject(value.columnName))
		}).toMap()

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
	fun parse(input: String?, replacements: Map<String, Any>): String {
		var result = input
		for ((key, value) in replacements) {
			result = result?.replace("$START_DELIMITER$key$STOP_DELIMITER", value.toString())
		}
		return result ?: ""
	}

	/**
	 * Converts a [BigInteger] value to a [Long].
	 *
	 * This method takes a [BigInteger] value and converts it to a [Long], ensuring that the conversion
	 * is exact. If the value cannot be exactly represented as a [Long], it will throw an exception.
	 *
	 * @param value The [BigInteger] value to convert.
	 * @return The converted [Long] value.
	 */
	fun parseToLong(value: Any?): Long = (value as BigInteger).longValueExact()
}
