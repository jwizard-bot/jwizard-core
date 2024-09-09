/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean

/**
 * Configuration class for initializing the database connection pool and JDBC template.
 *
 * This class provides configuration for the [HikariDataSource], which is used to manage the connection pool for
 * connecting to the database. It also provides a [JdbcTemplate] bean that is used to perform database operations.
 *
 * @property environmentBean The [EnvironmentBean] instance used to retrieve database connection properties from
 * 					 the environment.
 * @author Miłosz Gilga
 */
@Component
class DbSourceInitializerBean(private val environmentBean: EnvironmentBean) {

	/**
	 * Configures and creates a [HikariDataSource] bean.
	 *
	 * This method sets up the HikariCP connection pool with properties such as JDBC URL, username, password, and
	 * driver class name. The configuration values are retrieved from the [EnvironmentBean] instance.
	 *
	 * @return The configured [HikariDataSource] instance.
	 */
	@Bean
	fun dataSourceBean(): HikariDataSource {
		val config = HikariConfig()
		config.jdbcUrl = environmentBean.getProperty(BotProperty.DB_URL)
		config.username = environmentBean.getProperty(BotProperty.DB_USERNAME)
		config.password = environmentBean.getProperty(BotProperty.DB_PASSWORD)
		config.driverClassName = environmentBean.getProperty(BotProperty.DB_DRIVER_CLASS_NAME)
		config.maximumPoolSize = 10
		config.minimumIdle = 5
		config.idleTimeout = 30000
		config.connectionTimeout = 30000
		return HikariDataSource(config)
	}

	/**
	 * Configures and creates a [JdbcTemplateBean] bean.
	 *
	 * This method creates a [JdbcTemplateBean] instance using the configured [HikariDataSource]. The [JdbcTemplateBean]
	 * is used for executing SQL queries and updates, and provides a simplified way to interact with the database.
	 *
	 * @param dataSourceBean Configured [HikariDataSource] instance.
	 * @return The configured [JdbcTemplateBean] instance.
	 */
	@Bean
	fun jdbcTemplateBean(dataSourceBean: HikariDataSource) = JdbcTemplateBean(dataSourceBean)
}
