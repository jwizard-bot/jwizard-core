/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence

import java.sql.SQLType

/**
 * Represents a SQL column with a value and type.
 *
 * This data class holds the value and SQL type for a column used in SQL operations. It provides a way to store
 * and manage column data and its corresponding SQL type.
 *
 * @property value The value to be used for this column.
 * @property type The [SQLType] representing the SQL type of the column.
 * @author Miłosz Gilga
 */
data class SqlColumn(
	val value: Any,
	val type: SQLType,
)
