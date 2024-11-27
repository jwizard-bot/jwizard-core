/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql

import pl.jwizard.jwc.core.config.DeploymentDetails
import pl.jwizard.jwc.core.config.spi.VcsDeploymentSupplier
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.persistence.sql.JdbiQueryBean

/**
 * Implementation of [VcsDeploymentSupplier] that interacts with a relational database to retrieve deployment
 * information about specific repositories.
 *
 * @property jdbiQuery Bean for executing SQL queries.
 * @author Miłosz Gilga
 */
@SingletonComponent
class VcsDeploymentSupplierBean(private val jdbiQuery: JdbiQueryBean) : VcsDeploymentSupplier {

	/**
	 * Retrieves the latest deployment version for the specified repository from the database.
	 *
	 * The version is stored in the `latest_version_long` column of the `projects` table,
	 * and corresponds to the provided repository's ID.
	 *
	 * @param repositoryName The repository name for which to fetch the deployment version.
	 * @return The deployment version as a [String], or `null` if no version is found.
	 */
	override fun getDeploymentVersion(repositoryName: String): String? {
		val sql = "SELECT latest_version_long FROM projects WHERE name = ?"
		return jdbiQuery.queryForNullableObject(sql, String::class, repositoryName)
	}

	/**
	 * Retrieves detailed deployment information for the specified repository, including the latest commit SHA (`longSHA`)
	 * and the last update timestamp (`last_updated_utc`). This data is stored in the `projects` table and queried based
	 * on the repository ID.
	 *
	 * @param repositoryName The repository name for which to retrieve deployment details.
	 * @return A [DeploymentDetails] object containing the latest commit SHA and update timestamp, or `null` if no
	 *         details are available.
	 */
	override fun getDeploymentDetails(repositoryName: String): DeploymentDetails? {
		val sql = """
			SELECT latest_version_long longSHA, last_updated_utc lastUpdatedUtc
			FROM projects WHERE name = ?
		"""
		return jdbiQuery.queryForObject(sql, DeploymentDetails::class, repositoryName)
	}
}
