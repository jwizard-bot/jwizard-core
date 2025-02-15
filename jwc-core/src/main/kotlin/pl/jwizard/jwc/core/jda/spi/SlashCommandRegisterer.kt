package pl.jwizard.jwc.core.jda.spi

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild

interface SlashCommandRegisterer {
	fun registerGlobalCommands(jda: JDA)

	fun registerGuildCommands(guild: Guild)
}
