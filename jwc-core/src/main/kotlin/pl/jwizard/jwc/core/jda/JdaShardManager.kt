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
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.audio.DistributedAudioClient
import pl.jwizard.jwc.core.jda.color.JdaColorsCache
import pl.jwizard.jwc.core.jda.emoji.BotEmojisCache
import pl.jwizard.jwc.core.jda.event.JdaEventListener
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.jvm.JvmDisposable
import pl.jwizard.jwl.jvm.JvmDisposableHook
import pl.jwizard.jwl.property.AppBaseListProperty
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.util.getUserIdFromTokenWithException
import pl.jwizard.jwl.util.logger

@Component
final class JdaShardManager(
	private val environment: BaseEnvironment,
	private val jdaColorStore: JdaColorsCache,
	private val ioCKtContextFactory: IoCKtContextFactory,
	private val botEmojisCache: BotEmojisCache,
) : JvmDisposable {
	companion object {
		private val log = logger<JdaShardManager>()
	}

	private final lateinit var shardManager: ShardManager
	private final lateinit var jdaToken: String

	private val jvmDisposableHook = JvmDisposableHook(this)

	fun createShardsManager(distributedAudioClient: DistributedAudioClient) {
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

		val eventListeners = ioCKtContextFactory
			.getBeansAnnotatedWith<EventListener, JdaEventListener>()

		log.info(
			"Load: {} JDA event listeners: {}.",
			eventListeners.size,
			eventListeners.map { it.javaClass.simpleName },
		)
		jdaToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)
		botEmojisCache.loadCustomEmojis(this)

		log.info("Load: {} gateway intents: {}.", gatewayIntents.size, gatewayIntents)
		log.info("Load: {} enabled cache flags: {}.", enabledCacheFlags.size, enabledCacheFlags)
		log.info("Load: {} disabled cache flags: {}.", disabledCacheFlags.size, disabledCacheFlags)

		val shardingMinId = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_START)
		val shardingMaxId = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_END)

		val totalShards = environment.getProperty<Int>(BotProperty.JDA_SHARDING_TOTAL_SHARDS)
		val shardsCount = (shardingMaxId - shardingMinId) + 1

		shardManager = DefaultShardManagerBuilder
			.create(jdaToken, gatewayIntents.map { GatewayIntent.valueOf(it) })
			.setUseShutdownNow(true)
			.setShardsTotal(totalShards)
			.setShards(shardingMinId, shardingMaxId)
			.setVoiceDispatchInterceptor(distributedAudioClient.voiceDispatchInterceptor)
			.enableCache(enabledCacheFlags.map { CacheFlag.valueOf(it) })
			.disableCache(disabledCacheFlags.map { CacheFlag.valueOf(it) })
			.setActivity(Activity.listening("Loading..."))
			.setStatus(OnlineStatus.ONLINE)
			.addEventListeners(*eventListeners.toTypedArray())
			.setBulkDeleteSplittingEnabled(true)
			.build()

		jvmDisposableHook.initHook()

		log.info("Init: {} shards for this process.", shardsCount)
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

	val runningShards: List<JDA>
		get() = shardManager.shards
}
