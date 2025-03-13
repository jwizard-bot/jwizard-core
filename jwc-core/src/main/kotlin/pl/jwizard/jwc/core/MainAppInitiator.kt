package pl.jwizard.jwc.core

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.audio.DistributedAudioClient
import pl.jwizard.jwc.core.jda.ActivitySplashesThread
import pl.jwizard.jwc.core.jda.JdaShardManager
import pl.jwizard.jwc.core.jda.spi.ChannelListenerGuard
import pl.jwizard.jwc.core.jda.spi.CommandsLoader
import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMappersCache
import pl.jwizard.jwl.AppInitiator
import pl.jwizard.jwl.server.HttpServer

@Component
internal class MainAppInitiator(
	private val httpServer: HttpServer,
	private val radioPlaybackMappersCache: RadioPlaybackMappersCache,
	private val commandsLoader: CommandsLoader,
	private val distributedAudioClient: DistributedAudioClient,
	private val jdaShardManager: JdaShardManager,
	private val activitySplashesThread: ActivitySplashesThread,
	private val channelListenerGuard: ChannelListenerGuard,
) : AppInitiator {
	override fun onInit() {
		httpServer.init(onServerStart = ::onServerStart)
	}

	private fun onServerStart() {
		// firstly, load components via reflection api
		radioPlaybackMappersCache.loadRadioPlaybackClasses()
		commandsLoader.loadClassesViaReflectionApi()

		// init audio client before initialized any shard
		distributedAudioClient.initClient()
		jdaShardManager.createShardsManager(distributedAudioClient)
		activitySplashesThread.initSplashesSequence()
		channelListenerGuard.initThreadPool()
	}
}
