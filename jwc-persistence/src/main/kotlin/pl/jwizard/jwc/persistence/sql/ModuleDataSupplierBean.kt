package pl.jwizard.jwc.persistence.sql

import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.persistence.sql.JdbiQueryBean
import java.math.BigInteger

@SingletonComponent
internal class ModuleDataSupplierBean(private val jdbiQuery: JdbiQueryBean) : ModuleDataSupplier {
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
