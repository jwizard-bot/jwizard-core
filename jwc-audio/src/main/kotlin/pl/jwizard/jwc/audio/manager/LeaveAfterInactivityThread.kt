/*
 * Copyright (c) 2025 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.manager

import pl.jwizard.jwc.audio.client.DistributedAudioClientBean
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jvm.thread.JvmFixedPayloadThreadExecutor
import pl.jwizard.jwc.core.util.floatingSecToMin
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.secToDTF
import pl.jwizard.jwl.util.logger

/**
 * Thread executor that handles the disconnection of the bot from a voice channel after a specified period of
 * inactivity in the music playback.
 *
 * @property guildMusicManager The guild music manager responsible for handling audio playback and state.
 * @property audioClient The client responsible for managing audio server nodes and audio connections.
 * @author Miłosz Gilga
 */
class LeaveAfterInactivityThread(
	private val guildMusicManager: GuildMusicManager,
	private val audioClient: DistributedAudioClientBean,
) : JvmFixedPayloadThreadExecutor<Pair<Long, GuildCommandContext>>() {

	companion object {
		private val log = logger<LeaveAfterInactivityThread>()
	}

	/**
	 * Executes the thread to disconnect from the voice channel after a defined period of inactivity.
	 *
	 * @param payload A pair consisting of the elapsed time in seconds and the [CommandBaseContext].
	 */
	override fun executeJvmThreadWithPayload(payload: Pair<Long, GuildCommandContext>) {
		val (timeSec, context) = payload
		if (context.selfMember.voiceState?.inAudioChannel() == false) {
			return // skip, when bot already leaved channel
		}
		val guild = context.guild
		val message = guildMusicManager.createEmbedBuilder()
			.setDescription(
				i18nLocaleSource = I18nResponseSource.LEAVE_END_PLAYBACK_QUEUE,
				args = mapOf("elapsed" to floatingSecToMin(timeSec))
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		guildMusicManager.state.audioScheduler.stopAndDestroy().subscribe()
		audioClient.disconnectWithAudioChannel(guild)

		log.jdaInfo(
			guildMusicManager.state.context,
			"Leaved voice channel after: %s time of inactivity.",
			secToDTF(timeSec)
		)
		guildMusicManager.sendMessage(message)
	}
}
