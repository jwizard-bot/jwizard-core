package pl.jwizard.jwc.audio.event

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwl.util.logger

@JdaEventListenerBean
internal class DjRoleGuildEventListenerBean(
	private val environment: EnvironmentBean,
	private val jdaColorStore: JdaColorsCacheBean,
) : ListenerAdapter() {

	companion object {
		private val log = logger<DjRoleGuildEventListenerBean>()
	}

	override fun onGuildReady(event: GuildReadyEvent) {
		val guild = event.guild
		val guildDjRoleName = getDjRoleName(guild)
		val djRoles = guild.getRolesByName(guildDjRoleName, false)
		if (djRoles.isNotEmpty()) {
			return
		}
		generateDjRole(guild, guildDjRoleName).queue {
			guild.modifyRolePositions().selectPosition(it).moveTo(0).queue()
		}
	}

	override fun onRoleDelete(event: RoleDeleteEvent) {
		val deletedRole = event.role
		val guild = event.guild
		val guildDjRoleName = getDjRoleName(guild)
		if (deletedRole.name != guildDjRoleName) {
			return
		}
		generateDjRole(guild, guildDjRoleName).queue {
			log.info("Re-create DJ role for guild: {} after deleted.", guild.qualifier)
		}
	}

	private fun generateDjRole(guild: Guild, roleName: String) = guild.createRole()
		.setName(roleName)
		.setColor(jdaColorStore.getHexColor(JdaColor.PRIMARY))

	private fun getDjRoleName(
		guild: Guild,
	) = environment.getGuildProperty<String>(GuildProperty.DJ_ROLE_NAME, guild.idLong)
}
