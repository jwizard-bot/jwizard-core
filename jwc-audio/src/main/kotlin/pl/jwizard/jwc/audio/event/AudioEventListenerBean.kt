/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.event

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pl.jwizard.jwc.audio.manager.MusicManagersBean
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.jda.spi.JdaInstance

/**
 * Event listener for managing audio events in a Discord guild. This listener automatically handles bot deafen actions
 * when joining a voice channel and pauses/resumes music playback when the bot is muted or unmuted.
 *
 * @property audioChannelsListenerGuardBean A guard responsible for handling additional voice channel update events.
 * @property musicManagersBean Provides access to the cached music manager for controlling audio playback.
 * @property jdaInstance JDA instance for interacting with Discord API and retrieving guild information.
 * @author Miłosz Gilga
 */
@JdaEventListenerBean
class AudioEventListenerBean(
	private val audioChannelsListenerGuardBean: AudioChannelsListenerGuardBean,
	private val musicManagersBean: MusicManagersBean,
	private val jdaInstance: JdaInstance,
) : ListenerAdapter() {

	/**
	 * Handles voice channel update events. When the bot joins a voice channel, it automatically deafens itself to avoid
	 * listening to other members in the channel. Also triggers the listener guard for additional event handling.
	 *
	 * @param event The event containing details about the voice channel update.
	 */
	override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
		val guild = event.guild
		if (event.channelJoined != null && event.member.id == guild.selfMember.id) { // join to channel
			guild.selfMember.deafen(true).queue()
		}
		audioChannelsListenerGuardBean.onEveryVoiceUpdate(event)
	}

	/**
	 * Handles the bot being muted or unmuted in the voice channel. When the bot is muted, it pauses the currently
	 * playing track. When unmuted, it resumes playback. The status change is communicated via an embedded message sent
	 * to the text channel.
	 *
	 * @param event The event containing details about the mute state change.
	 */
	override fun onGuildVoiceMute(event: GuildVoiceMuteEvent) {
		val botMember = event.guild.selfMember
		val voiceState = event.guild.selfMember.voiceState
		if (event.member.idLong != event.guild.selfMember.idLong || voiceState == null) {
			return
		}
		musicManagersBean.getCachedMusicManager(event.guild.idLong)?.let { // perform only if music manager is cached
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
