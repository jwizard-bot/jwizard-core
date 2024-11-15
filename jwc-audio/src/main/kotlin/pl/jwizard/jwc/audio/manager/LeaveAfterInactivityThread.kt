/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.manager

import net.dv8tion.jda.api.entities.Guild
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jvm.thread.JvmFixedPayloadThreadExecutor
import pl.jwizard.jwc.core.util.floatingSecToMin
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.secToDTF
import pl.jwizard.jwl.util.logger

/**
 * Thread executor that handles the disconnection of the bot from a voice channel after a specified period of
 * inactivity in the music playback.
 *
 * @property musicManager The music manager responsible for handling audio playback and state.
 * @author Miłosz Gilga
 */
class LeaveAfterInactivityThread(
	private val musicManager: GuildMusicManager,
) : JvmFixedPayloadThreadExecutor<Pair<Long, Guild?>>() {

	companion object {
		private val log = logger<LeaveAfterInactivityThread>()
	}

	/**
	 * Executes the thread to disconnect from the voice channel after a defined period of inactivity.
	 *
	 * @param payload A pair consisting of the elapsed time in seconds and the guild from which to disconnect.
	 */
	override fun executeJvmThreadWithPayload(payload: Pair<Long, Guild?>) {
		val (timeSec, guild) = payload

		val message = musicManager.createEmbedBuilder()
			.setDescription(
				i18nLocaleSource = I18nResponseSource.LEAVE_END_PLAYBACK_QUEUE,
				args = mapOf("elapsed" to floatingSecToMin(timeSec))
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		musicManager.state.audioScheduler.stopAndDestroy()
		guild?.let { musicManager.beans.jdaShardManager.getDirectAudioController(guild)?.disconnect(it) }

		log.jdaInfo(musicManager.state.context, "Leaved voice channel after: %s time of inactivity.", secToDTF(timeSec))
		musicManager.sendMessage(message)
	}
}
