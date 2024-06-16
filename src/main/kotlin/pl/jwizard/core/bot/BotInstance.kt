/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.stereotype.Component
import pl.jwizard.core.audio.AloneOnChannelListener
import pl.jwizard.core.audio.player.AudioPlayerManager
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.properties.BotProperties
import pl.jwizard.core.command.reflect.CommandReflectLoader
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.seq.ActivityStatusSequencer
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

@Component
class BotInstance(
	private val botProperties: BotProperties,
	private val commandReflectLoader: CommandReflectLoader,
	private val botEventHandler: BotEventHandler,
	private val aloneOnChannelListener: AloneOnChannelListener,
	private val activityStatusSequencer: ActivityStatusSequencer,
	private val audioPlayerManager: AudioPlayerManager,
	private val playerManager: PlayerManager,
	private val botConfiguration: BotConfiguration,
) : AbstractLoggingBean(BotInstance::class) {

	private lateinit var jda: JDA

	fun start() {
		log.info("Bot instance is warming up...")
		try {
			commandReflectLoader.loadCommandsAndCheckDataIntegrity()
			commandReflectLoader.loadCommandsViaReflectionApi()

			jda = JDABuilder
				.create(botProperties.instance.authToken, GATEWAY_INTENTS)
				.enableCache(ENABLED_CACHE_FLAGS)
				.disableCache(DISABLED_CACHE_FLAGS)
				.setActivity(Activity.listening("Loading..."))
				.setStatus(OnlineStatus.ONLINE)
				.addEventListeners(botEventHandler, botConfiguration.eventWaiter)
				.setBulkDeleteSplittingEnabled(true)
				.build()
				.awaitReady()

			botConfiguration.setTitleAndIcon(jda)

			activityStatusSequencer.loadSplashes()
			activityStatusSequencer.initFixedDelay(jda)

			audioPlayerManager.initialize()
			aloneOnChannelListener.initialize(jda)

			log.info("Add bot into Discord server via link: {}", jda.getInviteUrl(PERMISSIONS))
			log.info("Started listening incoming requests...")
		} catch (ex: LoginException) {
			printErrorAndExit("Unable to login via passed token and application id parameters. Cause: ${ex.message}")
		} catch (ex: RuntimeException) {
			printErrorAndExit("Unexpected error occured. Cause: ${ex.message}")
		} catch (ex: InterruptedException) {
			printErrorAndExit("JDA Websocket threadpool connecting was interrupted. Cause: ${ex.message}")
		}
	}

	fun shutdown(event: ShutdownEvent) {
		if (event.jda.status == JDA.Status.SHUTTING_DOWN) {
			log.info("Shutting down bot instance...")
			for (guild in event.jda.guilds) {
				playerManager.findMusicManager(guild.id)?.actions?.clearAndDestroy(false)
				guild.audioManager.closeAudioConnection()
			}
			botConfiguration.threadPool.shutdownNow()
			event.jda.shutdown()
			log.info("Threadpool was cleared and current bot instance was terminated")
			exitProcess(0)
		}
	}

	private fun printErrorAndExit(message: String) {
		log.error(message)
		exitProcess(-1)
	}

	companion object {
		private val GATEWAY_INTENTS = arrayListOf(
			GatewayIntent.DIRECT_MESSAGES,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.GUILD_MESSAGE_REACTIONS,
			GatewayIntent.GUILD_VOICE_STATES,
			GatewayIntent.GUILD_MESSAGE_TYPING,
			GatewayIntent.GUILD_MEMBERS,
		)
		private val PERMISSIONS = arrayListOf(
			Permission.VIEW_CHANNEL,
			Permission.MESSAGE_SEND,
			Permission.MESSAGE_HISTORY,
			Permission.MESSAGE_ADD_REACTION,
			Permission.MESSAGE_EMBED_LINKS,
			Permission.MESSAGE_ATTACH_FILES,
			Permission.MESSAGE_MANAGE,
			Permission.MESSAGE_EXT_EMOJI,
			Permission.MANAGE_CHANNEL,
			Permission.VOICE_CONNECT,
			Permission.VOICE_SPEAK,
			Permission.USE_APPLICATION_COMMANDS,
			Permission.MANAGE_ROLES,
			Permission.VOICE_DEAF_OTHERS,
		)
		private val ENABLED_CACHE_FLAGS = arrayListOf(
			CacheFlag.MEMBER_OVERRIDES,
			CacheFlag.VOICE_STATE,
		)
		private val DISABLED_CACHE_FLAGS = arrayListOf(
			CacheFlag.ACTIVITY,
			CacheFlag.CLIENT_STATUS,
			CacheFlag.EMOJI,
			CacheFlag.ONLINE_STATUS,
			CacheFlag.SCHEDULED_EVENTS,
			CacheFlag.STICKER,
		)
	}
}
