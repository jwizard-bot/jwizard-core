package pl.jwizard.jwc.core.deployment

interface DeploymentSupplier {
	fun getDeploymentVersion(repositoryName: String): String?

	fun getDeploymentDetails(repositoryName: String): DeploymentDetails?
}
