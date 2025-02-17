package pl.jwizard.jwc.audio.manager

import pl.jwizard.jwc.audio.client.DistributedAudioClientImpl
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jvm.thread.JvmFixedPayloadThreadExecutor
import pl.jwizard.jwc.core.util.floatingSecToMin
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.secToDTF
import pl.jwizard.jwl.util.logger

class LeaveAfterInactivityThread(
	private val guildMusicManager: GuildMusicManager,
	private val audioClient: DistributedAudioClientImpl,
) : JvmFixedPayloadThreadExecutor<Pair<Long, GuildCommandContext>>() {
	companion object {
		private val log = logger<LeaveAfterInactivityThread>()
	}

	override fun executeJvmThreadWithPayload(payload: Pair<Long, GuildCommandContext>) {
		val (timeSec, context) = payload
		if (!audioClient.inAudioChannel(context.selfMember)) {
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
