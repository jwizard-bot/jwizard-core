package pl.jwizard.jwc.core.jda

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag
import pl.jwizard.jwc.core.audio.spi.DistributedAudioClient
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.jda.emoji.BotEmojisCacheBean
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.jvm.JvmDisposable
import pl.jwizard.jwl.jvm.JvmDisposableHook
import pl.jwizard.jwl.property.AppBaseListProperty
import pl.jwizard.jwl.util.getUserIdFromTokenWithException
import pl.jwizard.jwl.util.logger

@SingletonComponent
final class JdaShardManagerBean(
	private val environment: EnvironmentBean,
	private val jdaColorStore: JdaColorsCacheBean,
	private val ioCKtContextFactory: IoCKtContextFactory,
	private val botEmojisCache: BotEmojisCacheBean,
) : JvmDisposable {

	companion object {
		private val log = logger<JdaShardManagerBean>()
	}

	private final lateinit var shardManager: ShardManager
	private final lateinit var jdaToken: String

	private val jvmDisposableHook = JvmDisposableHook(this)

	fun createShardsManager(distributedAudioClientSupplier: DistributedAudioClient) {
		log.info("JDA instance is warming up...")
		jdaColorStore.loadColors()

		val gatewayIntents = environment.getListProperty<String>(BotListProperty.JDA_GATEWAY_INTENTS)
		val enabledCacheFlags = environment
			.getListProperty<String>(BotListProperty.JDA_CACHE_FLAGS_ENABLED)
		val disabledCacheFlags = environment
			.getListProperty<String>(BotListProperty.JDA_CACHE_FLAGS_DISABLED)
		val permissionFlags = environment.getListProperty<String>(AppBaseListProperty.JDA_PERMISSIONS)

		val permissions = permissionFlags.map { Permission.valueOf(it) }
		log.info("Load: {} JDA permissions.", permissions.size)

		val eventListeners =
			ioCKtContextFactory.getBeansAnnotatedWith<EventListener, JdaEventListenerBean>()
		log.info(
			"Load: {} JDA event listeners: {}.",
			eventListeners.size,
			eventListeners.map { it.javaClass.simpleName })

		jdaToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)
		botEmojisCache.loadCustomEmojis(this)

		log.info("Load: {} gateway intents: {}.", gatewayIntents.size, gatewayIntents)
		log.info("Load: {} enabled cache flags: {}.", enabledCacheFlags.size, enabledCacheFlags)
		log.info("Load: {} disabled cache flags: {}.", disabledCacheFlags.size, disabledCacheFlags)

		val shardingMinId = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_START)
		val shardingMaxId = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_END)
		val shardsCount = (shardingMaxId - shardingMinId) + 1

		shardManager = DefaultShardManagerBuilder
			.create(jdaToken, gatewayIntents.map { GatewayIntent.valueOf(it) })
			.setUseShutdownNow(true)
			.setShardsTotal(shardsCount)
			.setShards(shardingMinId, shardingMaxId)
			.setVoiceDispatchInterceptor(distributedAudioClientSupplier.voiceDispatchInterceptor)
			.enableCache(enabledCacheFlags.map { CacheFlag.valueOf(it) })
			.disableCache(disabledCacheFlags.map { CacheFlag.valueOf(it) })
			.setActivity(Activity.listening("Loading..."))
			.setStatus(OnlineStatus.ONLINE)
			.addEventListeners(*eventListeners.toTypedArray())
			.setBulkDeleteSplittingEnabled(true)
			.build()

		jvmDisposableHook.initHook()

		val clusterName = environment.getProperty<String>(BotProperty.JDA_SHARDING_CLUSTER)
		log.info("Init: {} shards for cluster: {}.", shardsCount, clusterName)
		log.info("Init shards manager from shard id: {} to: {}.", shardingMinId, shardingMaxId)
	}

	override fun cleanBeforeDisposeJvm() {
		val shardsCount = shardManager.shards.size
		shardManager.shutdown()
		log.info("JDA instances in: {} shards change was disposed.", shardsCount)
	}

	fun setPresenceActivity(activity: String) {
		shardManager.shards.forEach { it.presence.activity = Activity.listening(activity) }
	}

	fun getGuildById(guildId: Long) = shardManager.getGuildById(guildId)

	fun getUserById(userId: Long) = shardManager.getUserById(userId)

	fun getSelfUserId() = getUserIdFromTokenWithException(jdaToken)

	fun getDirectAudioController(guild: Guild) =
		shardManager.getShardById(guild.jda.shardInfo.shardId)?.directAudioController

	fun getShards(): List<JDA> = shardManager.shards
}
