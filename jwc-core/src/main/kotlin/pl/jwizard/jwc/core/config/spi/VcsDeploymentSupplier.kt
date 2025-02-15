package pl.jwizard.jwc.core.config.spi

import pl.jwizard.jwc.core.config.DeploymentDetails

interface VcsDeploymentSupplier {
	fun getDeploymentVersion(repositoryName: String): String?

	fun getDeploymentDetails(repositoryName: String): DeploymentDetails?
}
