package pl.jwizard.jwc.command.spi

import java.math.BigInteger

interface CommandDataSupplier {
	fun getDisabledGuildCommands(guildDbId: BigInteger, slashCommands: Boolean): List<Long>

	fun isCommandDisabled(guildDbId: BigInteger, commandId: Long, slashCommand: Boolean): Boolean
}
