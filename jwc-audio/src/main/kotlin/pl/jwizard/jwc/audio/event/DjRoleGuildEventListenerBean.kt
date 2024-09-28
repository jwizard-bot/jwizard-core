/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.event

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.property.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier

/**
 * Listener for handling events related to the DJ role in a guild. Automatically creates a DJ role if it doesn't exist
 * when the guild becomes ready, or if the role is deleted.
 *
 * @property environmentBean Provides access to environment-specific configurations and properties.
 * @property jdaColorStoreBean Stores color information for customizing the DJ role color.
 * @author Miłosz Gilga
 */
@JdaEventListenerBean
class DjRoleGuildEventListenerBean(
	private val environmentBean: EnvironmentBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
) : ListenerAdapter() {

	companion object {
		private val log = LoggerFactory.getLogger(DjRoleGuildEventListenerBean::class.java)
	}

	/**
	 * Handles the event when a guild becomes ready. This method checks if a DJ role already exists. If it doesn't, it
	 * creates one and sets its position at the top of the roles list.
	 *
	 * @param event The event triggered when a guild becomes ready.
	 */
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

	/**
	 * Handles the event when a role is deleted in the guild. If the deleted role is the DJ role, it recreates the DJ
	 * role with the same name and logs the action.
	 *
	 * @param event The event triggered when a role is deleted in the guild.
	 */
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

	/**
	 * Generates the DJ role for the guild with the specified role name and applies the primary color from the
	 * [JdaColorStoreBean].
	 *
	 * @param guild The guild for which the DJ role is being created.
	 * @param roleName The name of the DJ role.
	 * @return The role creation action.
	 */
	private fun generateDjRole(guild: Guild, roleName: String) = guild.createRole()
		.setName(roleName)
		.setColor(jdaColorStoreBean.getHexColor(JdaColor.PRIMARY))

	/**
	 * Retrieves the name of the DJ role from the guild's configuration properties.
	 *
	 * @param guild The guild from which the DJ role name is fetched.
	 * @return The name of the DJ role as a string.
	 */
	private fun getDjRoleName(guild: Guild) = environmentBean
		.getGuildProperty<String>(GuildProperty.DJ_ROLE_NAME, guild.idLong)
}
