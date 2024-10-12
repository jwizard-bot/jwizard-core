/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

import pl.jwizard.jwc.core.jda.command.CommandBaseContext

/**
 * Interface for supplying music managers, responsible for managing audio playback and interactions within a Discord
 * guild.
 *
 * @author Miłosz Gilga
 */
interface MusicManagersSupplier {

	/**
	 * Retrieves or creates a music manager for the specified command context.
	 *
	 * @param context The command context containing information about the command execution environment.
	 * @param distributedAudioClientSupplier The supplier responsible for providing audio client nodes.
	 * @return The music manager associated with the specified context.
	 */
	fun getOrCreateMusicManager(
		context: CommandBaseContext,
		distributedAudioClientSupplier: DistributedAudioClientSupplier
	): MusicManager
}
