package pl.jwizard.jwc.command.spi

import java.math.BigInteger

interface ModuleDataSupplier {
	fun getDisabledGuildModules(guildDbId: BigInteger): List<Long>

	fun isDisabled(moduleId: Long, guildDbId: BigInteger): Boolean
}
