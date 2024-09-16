/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.spi.JdaPermissionFlagsSupplier
import pl.jwizard.jwc.persistence.JdbcKtTemplateBean

/**
 * Implementation of the [JdaPermissionFlagsSupplier] interface for retrieving permission flags from a database
 * using JDBC.
 *
 * @property jdbcKtTemplateBean The [JdbcKtTemplateBean] used to execute SQL queries against the database.
 * @author Miłosz Gilga
 */
@Component
class JdaPermissionFlagsSupplierBean(private val jdbcKtTemplateBean: JdbcKtTemplateBean) : JdaPermissionFlagsSupplier {

	/**
	 * Retrieves a list of active permission flags from the database.
	 *
	 * This method executes a SQL query to select the flag names from the `jda_permission_flags` table where the
	 * `is_active` column is set to true. It then returns these flag names as a list of strings.
	 *
	 * @return A list of active permission flag names.
	 */
	override fun getPermissionFlags(): List<String> {
		val sql = "SELECT flag_name FROM jda_permission_flags WHERE is_active = true"
		return jdbcKtTemplateBean.queryForList(sql, String::class.java)
	}
}
