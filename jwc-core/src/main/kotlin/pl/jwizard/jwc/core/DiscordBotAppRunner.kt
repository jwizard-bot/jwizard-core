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
import pl.jwizard.jwl.SpringKtContextFactory

/**
 * The main class that loads resources, configuration and runs the bot instance.
 * Use this class with [AppContextInitiator] annotation. Singleton instance.
 *
 * @author Miłosz Gilga
 */
object DiscordBotAppRunner : AppRunner() {

	/**
	 * Executes the application logic with the provided Spring context. This method is responsible for starts loading
	 * configuration, resources and a created of new JDA bot instance.
	 *
	 * @param context main type of class that runs the bot.
	 */
	override fun runWithContext(context: SpringKtContextFactory) {
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
		jdaInstance.configureMetadata()

		activitySplashes.initSplashesSequence()
		channelListenerGuard.initThreadPool()
	}
}
