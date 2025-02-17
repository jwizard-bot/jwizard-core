package pl.jwizard.jwc.persistence

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import pl.jwizard.jwl.persistence.sql.DbSourceInitializer
import pl.jwizard.jwl.persistence.sql.JdbiQuery
import pl.jwizard.jwl.property.BaseEnvironment

@Component
internal class SqlDataSourceConfiguration {
	@Bean
	fun jdbiQuery(environment: BaseEnvironment): JdbiQuery {
		val initializer = DbSourceInitializer()
		val jdbi = initializer.createJdbi(environment)
		return JdbiQuery(jdbi)
	}
}
