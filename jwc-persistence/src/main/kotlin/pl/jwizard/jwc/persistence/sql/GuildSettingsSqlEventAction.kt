package pl.jwizard.jwc.persistence.sql

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.spi.GuildSettingsEventAction
import pl.jwizard.jwl.persistence.sql.JdbiQuery
import pl.jwizard.jwl.persistence.sql.SqlColumn
import pl.jwizard.jwl.property.AppBaseListProperty
import pl.jwizard.jwl.property.AppBaseProperty
import pl.jwizard.jwl.property.BaseEnvironment
import java.sql.JDBCType

@Component
internal class GuildSettingsSqlEventAction(
	private val jdbiQuery: JdbiQuery,
	private val environment: BaseEnvironment,
) : GuildSettingsEventAction {
	override fun createGuildSettings(guildId: Long, guildLocale: String): Pair<Boolean, String?> {
		val guildSettingsAlreadyExist = jdbiQuery.queryForBool(
			sql = "SELECT COUNT(*) > 0 FROM guilds WHERE discord_id = ?",
			guildId,
		)
		if (guildSettingsAlreadyExist) {
			return Pair(false, null)
		}
		val availableLanguages = environment.getListProperty<String>(AppBaseListProperty.I18N_LANGUAGES)
		val guildLanguage = guildLocale.substring(0, 2)
		val language = if (guildLanguage in availableLanguages) {
			guildLanguage
		} else {
			environment.getProperty<String>(AppBaseProperty.I18N_DEFAULT_LANGUAGE)
		}
		return jdbiQuery.inTransaction<Pair<Boolean, String?>> {
			try {
				val columns = mapOf(
					"discord_id" to SqlColumn(guildId, JDBCType.BIGINT),
					"language" to SqlColumn(language, JDBCType.VARCHAR),
				)
				jdbiQuery.insertMultiples("guilds", columns)
				Pair(true, null)
			} catch (ex: Exception) {
				it.rollback()
				Pair(false, ex.message)
			}
		}
	}

	override fun deleteDefaultMusicTextChannel(
		guildId: Long,
	): Int = jdbiQuery.update("UPDATE guilds SET music_text_channel_id = NULL where id = ?", guildId)

	override fun deleteGuildSettings(
		guildId: Long,
	): Int = jdbiQuery.update("DELETE FROM guilds WHERE discord_id = ?", guildId)
}
