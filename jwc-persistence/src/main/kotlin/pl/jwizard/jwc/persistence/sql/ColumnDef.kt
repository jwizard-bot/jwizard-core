/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql

import kotlin.reflect.KClass

/**
 * Represents a definition of a database column, including its name and type.
 *
 * @param T The type of the column's value.
 * @property columnName The name of the column in the database.
 * @property type The Kotlin class representing the type of the column's value.
 * @author Miłosz Gilga
 */
data class ColumnDef<T : Any>(
	val columnName: String,
	val type: KClass<T>,
)
