/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.config

/**
 * A data class holding deployment details of the application, typically fetched from version control or a deployment
 * tracking system.
 *
 * @property longSHA The full SHA hash of the commit, indicating the specific version or snapshot of the codebase.
 * @property lastUpdatedUtc A timestamp (in UTC) of the last deployment or commit, providing a reference to when this
 *           version was last updated.
 * @author Miłosz Gilga
 */
data class DeploymentDetails(
	val longSHA: String?,
	val lastUpdatedUtc: String?,
)
