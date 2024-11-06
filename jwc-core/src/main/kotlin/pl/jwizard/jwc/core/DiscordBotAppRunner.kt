/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core

import pl.jwizard.jwc.core.audio.spi.DistributedAudioClientSupplier
import pl.jwizard.jwc.core.jda.ActivitySplashesBean
import pl.jwizard.jwc.core.jda.JdaInstanceBean
import pl.jwizard.jwc.core.jda.spi.ChannelListenerGuard
import pl.jwizard.jwc.core.jda.spi.CommandsLoader
import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMappersCache
import pl.jwizard.jwl.AppContextInitiator
import pl.jwizard.jwl.AppRunner
import pl.jwizard.jwl.IoCKtContextFactory
import pl.jwizard.jwl.server.HttpServer
import pl.jwizard.jwl.server.HttpServerHook

/**
 * The main class that loads resources, configuration and runs the bot instance. Use this class with
 * [AppContextInitiator] annotation. Singleton instance.
 *
 * @author Miłosz Gilga
 */
object DiscordBotAppRunner : AppRunner(), HttpServerHook {

	/**
	 * Initializes the HTTP server and attaches this class as a hook to be executed after the server start.
	 *
	 * @param context The pre-configured [IoCKtContextFactory] class representing the IoC context.
	 */
	override fun runWithContext(context: IoCKtContextFactory) {
		val httpServer = context.getBean(HttpServer::class)
		httpServer.init(this)
	}

	/**
	 * Performs tasks after the HTTP server has started, such as initializing various services and components of the bot,
	 * including JDA instance, radio playback, command loading, and audio client nodes.
	 *
	 * @param context The pre-configured [IoCKtContextFactory] class representing the IoC context.
	 */
	override fun afterStartServer(context: IoCKtContextFactory) {
		val jdaInstance = context.getBean(JdaInstanceBean::class)
		val activitySplashes = context.getBean(ActivitySplashesBean::class)

		val radioPlaybackMappersCache = context.getBean(RadioPlaybackMappersCache::class)
		val commandLoader = context.getBean(CommandsLoader::class)
		val audioClientSupplier = context.getBean(DistributedAudioClientSupplier::class)
		val channelListenerGuard = context.getBean(ChannelListenerGuard::class)

		radioPlaybackMappersCache.loadRadioPlaybackClasses()
		commandLoader.loadClassesViaReflectionApi()

		audioClientSupplier.initClientNodes()

		jdaInstance.createJdaWrapper(audioClientSupplier)

		activitySplashes.initSplashesSequence()
		channelListenerGuard.initThreadPool()
	}
}
