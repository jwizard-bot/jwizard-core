package pl.jwizard.jwc.core.deployment

import org.springframework.stereotype.Component
import pl.jwizard.jwl.property.AppBaseProperty
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.property.VcsProperty

@Component
class DeploymentInfo(
	private val deploymentSupplier: DeploymentSupplier,
	environment: BaseEnvironment,
) {
	private val orgName = environment.getProperty<String>(AppBaseProperty.VCS_ORGANIZATION_NAME)
	private val repoName = environment.getProperty<String>(VcsProperty.VCS_REPOSITORY_JW_CORE)

	fun getCoreDeploymentDetails() = deploymentSupplier.getDeploymentDetails(repoName)

	fun createCoreDeploymentGitHubNameAndUrl(): Pair<String?, String?> {
		val deploymentVersion = deploymentSupplier.getDeploymentVersion(repoName)
		val name = deploymentVersion?.substring(0, 7)
		val url = deploymentVersion?.let { "https://github.com/$orgName/$repoName/tree/$it" }
		return Pair(name, url)
	}
}
