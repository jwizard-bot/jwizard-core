package pl.jwizard.jwc.persistence.sql

import pl.jwizard.jwc.core.config.DeploymentDetails
import pl.jwizard.jwc.core.config.spi.VcsDeploymentSupplier
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.persistence.sql.JdbiQueryBean

@SingletonComponent
class VcsDeploymentSupplierBean(private val jdbiQuery: JdbiQueryBean) : VcsDeploymentSupplier {
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
