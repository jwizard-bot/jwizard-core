/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot

import pl.jwizard.core.audio.AloneOnChannelListener
import pl.jwizard.core.audio.AudioPlayerActivityEventsHandler
import pl.jwizard.core.command.CommandProxyListener
import pl.jwizard.core.command.SlashCommandRegisterer
import pl.jwizard.core.command.action.ActionProxyListener
import pl.jwizard.core.settings.GuildSettings
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.events.ShutdownEvent
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent
import net.dv8tion.jda.api.events.guild.GuildBanEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

@Component
class BotEventHandler(
	@Lazy private val botInstance: BotInstance,
	private val aloneOnChannelListener: AloneOnChannelListener,
	private val guildSettings: GuildSettings,
	private val audioPlayerActivityEventsHandler: AudioPlayerActivityEventsHandler,
	private val commandProxyListener: CommandProxyListener,
	private val actionProxyListener: ActionProxyListener,
	private val slashCommandRegisterer: SlashCommandRegisterer,
) : ListenerAdapter() {

	override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) = commandProxyListener.onRegularCommand(event)

	override fun onSlashCommand(event: SlashCommandEvent) = commandProxyListener.onSlashCommand(event)

	override fun onButtonClick(event: ButtonClickEvent) = actionProxyListener.onPressButton(event)

	override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) = audioPlayerActivityEventsHandler.setBotDeafen(event)

	override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) = aloneOnChannelListener.onEveryVoiceUpdate(event)

	override fun onGuildVoiceMute(event: GuildVoiceMuteEvent) =
		audioPlayerActivityEventsHandler.stopPlayingContentAndFreeze(event)

	override fun onRoleDelete(event: RoleDeleteEvent) = audioPlayerActivityEventsHandler.recreateDjRoleOnDelete(event)

	override fun onTextChannelDelete(event: TextChannelDeleteEvent) =
		audioPlayerActivityEventsHandler.unsetMusicTextChannelOnDelete(event)

	override fun onGuildJoin(event: GuildJoinEvent) {
		guildSettings.getAndPersistGuildSettings(event.guild.id)
		audioPlayerActivityEventsHandler.createDjRoleOnJoin(event)
		slashCommandRegisterer.registerGuildCommands(event.guild)
	}

	override fun onGuildReady(event: GuildReadyEvent) {
		guildSettings.getAndPersistGuildSettings(event.guild.id)
		slashCommandRegisterer.registerGuildCommands(event.guild)
	}

	override fun onGuildLeave(event: GuildLeaveEvent) = guildSettings.deleteGuildSettings(event.guild.id)

	override fun onGuildBan(event: GuildBanEvent) = guildSettings.deleteGuildSettings(event.guild.id)

	override fun onShutdown(event: ShutdownEvent) = botInstance.shutdown(event)
}
