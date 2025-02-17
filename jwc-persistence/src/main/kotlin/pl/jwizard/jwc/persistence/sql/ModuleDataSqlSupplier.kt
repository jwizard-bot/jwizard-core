package pl.jwizard.jwc.persistence.sql

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwl.persistence.sql.JdbiQuery
import java.math.BigInteger

@Component
internal class ModuleDataSqlSupplier(private val jdbiQuery: JdbiQuery) : ModuleDataSupplier {
	override fun getDisabledGuildModules(guildDbId: BigInteger): List<Long> {
		val sql = "SELECT module_id FROM guilds_disabled_modules WHERE guild_id = ?"
		return jdbiQuery.queryForList(sql, Long::class, guildDbId)
	}

	override fun isDisabled(moduleId: Long, guildDbId: BigInteger): Boolean {
		val sql = """
			SELECT COUNT(*) > 0 FROM guilds_disabled_modules WHERE module_id = ? AND guild_id = ?
		"""
		return jdbiQuery.queryForBool(sql, moduleId, guildDbId)
	}
}
