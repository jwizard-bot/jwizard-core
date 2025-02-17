package pl.jwizard.jwc.persistence.sql

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.deployment.DeploymentDetails
import pl.jwizard.jwc.core.deployment.DeploymentSupplier
import pl.jwizard.jwl.persistence.sql.JdbiQuery

@Component
internal class DeploymentSqlSupplier(
	private val jdbiQuery: JdbiQuery,
) : DeploymentSupplier {
	override fun getDeploymentVersion(repositoryName: String): String? {
		val sql = "SELECT latest_version_long FROM projects WHERE name = ?"
		return jdbiQuery.queryForNullableObject(sql, String::class, repositoryName)
	}

	override fun getDeploymentDetails(repositoryName: String): DeploymentDetails? {
		val sql = """
			SELECT latest_version_long longSHA, last_updated_utc lastUpdatedUtc
			FROM projects WHERE name = ?
		"""
		return jdbiQuery.queryForObject(sql, DeploymentDetails::class, repositoryName)
	}
}
