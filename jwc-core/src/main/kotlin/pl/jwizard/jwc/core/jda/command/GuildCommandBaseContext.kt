package pl.jwizard.jwc.core.jda.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import java.math.BigInteger

interface GuildCommandBaseContext : CommandBaseContext {
	val guildDbId: BigInteger

	val guild: Guild

	val author: Member

	// alias for bot member
	val selfMember: Member

	// channel, where command was invoked
	val textChannel: TextChannel
}
