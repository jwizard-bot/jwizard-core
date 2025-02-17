package pl.jwizard.jwc.audio.event

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pl.jwizard.jwc.audio.manager.MusicManagersCache
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.event.JdaEventListener

@JdaEventListener
internal class AudioEventListener(
	private val audioChannelsListenerGuard: AudioChannelsListenerGuard,
	private val musicManagers: MusicManagersCache,
) : ListenerAdapter() {

	override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
		val guild = event.guild
		if (event.channelJoined != null && event.member.id == guild.selfMember.id) {
			// join to channel
			guild.selfMember.deafen(true).queue()
		}
		audioChannelsListenerGuard.onEveryVoiceUpdate(event)
	}

	override fun onGuildVoiceMute(event: GuildVoiceMuteEvent) {
		val botMember = event.guild.selfMember
		val voiceState = event.guild.selfMember.voiceState
		if (event.member.idLong != event.guild.selfMember.idLong || voiceState == null) {
			return
		}
		musicManagers.getCachedMusicManager(event.guild.idLong)?.let {
			// perform only if music manager is cached
			val musicManager = it
			val placeholder = if (botMember.voiceState?.isMuted == true) {
				I18nResponseSource.PAUSE_TRACK_ON_FORCE_MUTE
			} else {
				I18nResponseSource.RESUME_TRACK_ON_FORCE_UNMUTE
			}
			val embedMessage = musicManager.createEmbedBuilder()
				.setDescription(placeholder)
				.setColor(JdaColor.PRIMARY)
				.build()

			musicManager.createdOrUpdatedPlayer
				.setPaused(botMember.voiceState?.isMuted == true)
				.subscribe { musicManager.sendMessage(embedMessage) }
		}
	}
}
