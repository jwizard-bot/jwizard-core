/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.config.spi

import pl.jwizard.jwc.core.config.DeploymentDetails
import pl.jwizard.jwl.vcs.VcsRepository

/**
 * An interface for retrieving deployment information from a version control system (VCS).
 *
 * This supplier allows the system to access specific details about the application's deployment, such as the version
 * and metadata from a given VCS repository.
 *
 * @author Miłosz Gilga
 * @see VcsRepository for possible repository options.
 */
interface VcsDeploymentSupplier {

	/**
	 * Retrieves the deployment version string for the specified repository. This might be a short or human-readable
	 * version identifier, such as a tag name or version number.
	 *
	 * @param repositoryName The repository name from which to fetch the deployment version.
	 * @return The deployment version as a [String], or `null` if the version is not available.
	 */
	fun getDeploymentVersion(repositoryName: String): String?

	/**
	 * Fetches detailed deployment information, including commit SHA and last updated timestamp, for the specified
	 * repository. This information is used for tracking and debugging purposes.
	 *
	 * @param repositoryName The repository name for which to retrieve deployment details.
	 * @return A [DeploymentDetails] object containing commit SHA and timestamp, or `null` if details are unavailable.
	 */
	fun getDeploymentDetails(repositoryName: String): DeploymentDetails?
}
