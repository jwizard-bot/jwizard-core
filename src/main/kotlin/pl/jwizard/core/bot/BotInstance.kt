/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot

import javax.security.auth.login.LoginException
import kotlin.system.exitProcess
import pl.jwizard.core.audio.AloneOnChannelListener
import pl.jwizard.core.command.CommandProxyListener
import pl.jwizard.core.command.SlashCommandRegisterer
import pl.jwizard.core.command.reflect.CommandLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

@Component
class BotInstance(
	private val _botProperties: BotProperties,
	private val _commandLoader: CommandLoader,
	private val _commandProxyListener: CommandProxyListener,
	private val _slashCommandRegisterer: SlashCommandRegisterer,
	private val _botStatusEventHandler: BotStatusEventHandler,
	private val _aloneOnChannelListener: AloneOnChannelListener,
) {
	private lateinit var _jda: JDA

	fun start() {
		LOG.info("Bot instance is warming up...")
		try {
			_commandLoader.fetchCommandsFromApi()
			_commandLoader.reflectAndLoadCommands()

			val eventListeners = listOf(
				_botStatusEventHandler,
				_commandProxyListener,
				_slashCommandRegisterer
			)
			_jda = JDABuilder
				.create(_botProperties.instance?.authToken, GATEWAY_INTENTS)
				.enableCache(ENABLED_CACHE_FLAGS)
				.disableCache(DISABLED_CACHE_FLAGS)
				.setActivity(Activity.listening("Loading..."))
				.setStatus(OnlineStatus.ONLINE)
				.setBulkDeleteSplittingEnabled(true)
				.build()
			for (listener in eventListeners) {
				_jda.addEventListener(listener)
				LOG.info("Adding event listener: {} to JDA instance", listener::class.simpleName)
			}
			_jda.awaitReady()

			_aloneOnChannelListener.initialize(_jda)

			LOG.info("Add bot into Discord server via link: {}", _jda.getInviteUrl(PERMISSIONS))
			LOG.info("Started listening incoming requests...")
		} catch (ex: LoginException) {
			printErrorAndExit("Unable to login via passed token and application id parameters.")
		} catch (ex: RuntimeException) {
			printErrorAndExit("Unexpected error occured. Cause: ${ex.message}")
		} catch (ex: InterruptedException) {
			printErrorAndExit("JDA Websocket threadpool connecting was interrupted. Cause: ${ex.message}")
		}
	}

	private fun printErrorAndExit(message: String) {
		LOG.error(message)
		exitProcess(-1)
	}

	companion object {
		private val LOG: Logger = LoggerFactory.getLogger(BotInstance::class.java)
		private val GATEWAY_INTENTS = arrayListOf(
			GatewayIntent.DIRECT_MESSAGES,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.GUILD_MESSAGE_REACTIONS,
			GatewayIntent.GUILD_VOICE_STATES,
			GatewayIntent.GUILD_MESSAGE_TYPING,
			GatewayIntent.GUILD_MEMBERS,
		)
		private val PERMISSIONS = arrayListOf(
			Permission.MESSAGE_READ,
			Permission.MESSAGE_WRITE,
			Permission.MESSAGE_HISTORY,
			Permission.MESSAGE_ADD_REACTION,
			Permission.MESSAGE_EMBED_LINKS,
			Permission.MESSAGE_ATTACH_FILES,
			Permission.MESSAGE_MANAGE,
			Permission.MESSAGE_EXT_EMOJI,
			Permission.MANAGE_CHANNEL,
			Permission.VOICE_CONNECT,
			Permission.VOICE_SPEAK,
			Permission.USE_SLASH_COMMANDS,
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
			CacheFlag.EMOTE,
			CacheFlag.ONLINE_STATUS
		)
	}
}
