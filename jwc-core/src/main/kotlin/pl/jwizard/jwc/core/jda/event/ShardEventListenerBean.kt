/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.event

import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pl.jwizard.jwc.core.jda.spi.SlashCommandRegisterer

/**
 * Listener responsible for handling shard lifecycle events in the Discord bot.
 *
 * This class listens for the bot's shard readiness event and ensures that global slash commands are registered once
 * during the bot's startup process. It leverages the [SlashCommandRegisterer] to handle command registration.
 *
 * @property slashCommandRegisterer Handles the registration of global and guild-specific slash commands.
 * @author Miłosz Gilga
 */
@JdaEventListenerBean
class ShardEventListenerBean(private val slashCommandRegisterer: SlashCommandRegisterer) : ListenerAdapter() {

	/**
	 * Tracks whether global slash commands have already been registered.
	 *
	 * This prevents duplicate registrations during the bot's lifecycle, especially in a multi-shard environment.
	 */
	private var globalSlashCommandsAreSet = false

	/**
	 * Handles the shard readiness event.
	 *
	 * When a shard becomes ready, this method ensures that global slash commands are registered exactly once. In a
	 * multi-shard setup, only the first ready event will trigger the registration process.
	 *
	 * @param event The [ReadyEvent] triggered when a shard is ready.
	 */
	override fun onReady(event: ReadyEvent) {
		if (!globalSlashCommandsAreSet) {
			slashCommandRegisterer.registerGlobalCommands(event.jda)
			globalSlashCommandsAreSet = true
		}
	}
}
