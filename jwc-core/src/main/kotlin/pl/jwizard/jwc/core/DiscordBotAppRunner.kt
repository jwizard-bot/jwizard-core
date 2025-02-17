package pl.jwizard.jwc.core

import pl.jwizard.jwc.core.audio.DistributedAudioClient
import pl.jwizard.jwc.core.jda.ActivitySplashesThread
import pl.jwizard.jwc.core.jda.JdaShardManager
import pl.jwizard.jwc.core.jda.spi.ChannelListenerGuard
import pl.jwizard.jwc.core.jda.spi.CommandsLoader
import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMappersCache
import pl.jwizard.jwl.AppRunner
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.server.HttpServer

object DiscordBotAppRunner : AppRunner() {
	override fun runWithContext(context: IoCKtContextFactory) {
		val httpServer = context.getBean(HttpServer::class)
		httpServer.init(onServerStart = {
			val shardsManagerInstance = context.getBean(JdaShardManager::class)
			val activitySplashes = context.getBean(ActivitySplashesThread::class)

			val radioPlaybackMappersCache = context.getBean(RadioPlaybackMappersCache::class)
			val commandLoader = context.getBean(CommandsLoader::class)
			val distributedAudioClientSupplier = context.getBean(DistributedAudioClient::class)
			val channelListenerGuard = context.getBean(ChannelListenerGuard::class)

			radioPlaybackMappersCache.loadRadioPlaybackClasses()
			commandLoader.loadClassesViaReflectionApi()

			distributedAudioClientSupplier.initClient()

			shardsManagerInstance.createShardsManager(distributedAudioClientSupplier)

			activitySplashes.initSplashesSequence()
			channelListenerGuard.initThreadPool()
		})
	}
}
