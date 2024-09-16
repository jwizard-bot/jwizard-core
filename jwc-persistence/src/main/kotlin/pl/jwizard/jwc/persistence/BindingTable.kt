/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence

/**
 * Represents a binding table configuration with column details.
 *
 * This data class encapsulates the information needed to define a binding table, including the table name and the
 * names of the left and right columns used in the binding process.
 *
 * @property tableName The name of the table used in the binding operation.
 * @property leftColumnName The name of the left column in the binding table.
 * @property rightColumnName The name of the right column in the binding table.
 * @author Miłosz Gilga
 */
data class BindingTable(
	val tableName: String,
	val leftColumnName: String,
	val rightColumnName: String,
)
