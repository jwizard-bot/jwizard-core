/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql

import org.springframework.stereotype.Component
import pl.jwizard.jwc.exception.spi.ExceptionSupplier
import pl.jwizard.jwl.persistence.sql.ColumnDef
import pl.jwizard.jwl.persistence.sql.JdbcKtTemplateBean

/**
 * Implementation of [ExceptionSupplier] that retrieves exception tracker data from the database.
 *
 * This component interacts with the database to load exception tracking information, mapping each property key to its
 * corresponding tracker value.
 *
 * @property jdbcKtTemplateBean The JDBC template bean used for database operations.
 * @author Miłosz Gilga
 */
@Component
class ExceptionSupplierBean(private val jdbcKtTemplateBean: JdbcKtTemplateBean) : ExceptionSupplier {

	/**
	 * Loads the exception trackers from the database.
	 *
	 * Executes a SQL query to fetch property keys and their associated tracker values, returning them as a map where
	 * the keys are property keys and the values are corresponding tracker integers.
	 *
	 * @return A map of property keys to tracker integers.
	 */
	override fun loadTrackers(): Map<String, Int> = jdbcKtTemplateBean.queryForListMap(
		sql = "SELECT property_key propKey, tracker FROM exceptions",
		key = ColumnDef("propKey", String::class),
		value = ColumnDef("tracker", Int::class)
	)
}
