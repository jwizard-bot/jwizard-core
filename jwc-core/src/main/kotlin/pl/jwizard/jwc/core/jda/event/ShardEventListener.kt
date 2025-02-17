package pl.jwizard.jwc.core.jda.event

import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pl.jwizard.jwc.core.jda.spi.SlashCommandRegisterer

@JdaEventListener
class ShardEventListener(
	private val slashCommandRegisterer: SlashCommandRegisterer
) : ListenerAdapter() {
	private var globalSlashCommandsAreSet = false

	override fun onReady(event: ReadyEvent) {
		// register global slash commands only once (when first shard is initialized)
		// for other shards, ignore
		if (!globalSlashCommandsAreSet) {
			slashCommandRegisterer.registerGlobalCommands(event.jda)
			globalSlashCommandsAreSet = true
		}
	}
}
