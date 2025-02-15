package pl.jwizard.jwc.core.jda.spi

interface GuildSettingsEventAction {
	fun createGuildSettings(guildId: Long, guildLocale: String): Pair<Boolean, String?>

	fun deleteDefaultMusicTextChannel(guildId: Long): Int

	fun deleteGuildSettings(guildId: Long): Int
}
