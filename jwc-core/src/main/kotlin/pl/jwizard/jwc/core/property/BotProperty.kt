package pl.jwizard.jwc.core.property

import pl.jwizard.jwl.property.AppProperty
import kotlin.reflect.KClass

enum class BotProperty(
	override val key: String,
	override val type: KClass<*> = String::class,
) : AppProperty {
	// JDA secret token
	JDA_SECRET_TOKEN("jda.secret-token"),

	// instance name specified for running multiple instances
	JDA_INSTANCE_NAME("jda.instance.name"),

	// instance legacy prefix specified for running multiple instances
	JDA_INSTANCE_PREFIX("jda.instance.prefix"),

	// cluster name (also cluster key) for selected shard offsets
	JDA_SHARDING_CLUSTER("jda.sharding.cluster"),

	// start offset of JDA shard ID
	JDA_SHARDING_OFFSET_START("jda.sharding.offset.start", Int::class),

	// end offset of JDA shard ID
	JDA_SHARDING_OFFSET_END("jda.sharding.offset.end", Int::class),

	// JDA default activity
	// enabled when [JDA_SPLASHES_ENABLED] property is set to false
	JDA_DEFAULT_ACTIVITY("jda.default-activity"),

	// JDA splashes toggle boolean property. If true, splashes are enabled, otherwise show nothing
	JDA_SPLASHES_ENABLED("jda.splashes.enabled", Boolean::class),

	// JDA splashes interval in seconds
	JDA_SPLASHES_INTERVAL_SEC("jda.splashes.interval-sec", Long::class),

	// JDA primary color in embed messages
	JDA_COLOR_PRIMARY("jda.color.primary"),

	// JDA danger color in embed messages
	JDA_COLOR_DANGER("jda.color.danger"),

	// maximum embeds messages in single JDA command interaction
	JDA_INTERACTION_MESSAGE_MAX_EMBEDS("jda.interaction.message.max-embeds", Int::class),

	// maximum action rows in single JDA command message with custom interaction
	JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_ROWS(
		"jda.interaction.message.action-row.max-rows",
		Int::class
	),

	// maximum components in single action row in single JDA command message with custom interaction
	JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_COMPONENTS_IN_ROW(
		"jda.interaction.message.action-row.max-components-in-row",
		Int::class
	),

	// the delay time (in seconds) before disabling interaction components in JDA
	JDA_INTERACTION_MESSAGE_COMPONENT_DISABLE_DELAY_SEC(
		"jda.interaction.message.component.disable-delay-sec",
		Long::class
	),

	// JDA maximum number of options in single autocomplete interaction request
	JDA_INTERACTION_SLASH_AUTOCOMPLETE_MAX_OPTIONS(
		"jda.interaction.slash.autocomplete.max-options",
		Int::class
	),

	// defines chunk size for custom paginator for embed messages in command handlers
	JDA_PAGINATION_CHUNK_SIZE("jda.pagination.chunk-size", Int::class),

	// the official website for the bot
	LINK_WEBSITE("link.website"),

	// link to the bot external status page
	LINK_STATUS("link.status"),

	// the repository URL where the bot's source code is hosted
	LINK_REPOSITORY("link.repository"),

	// fragment link to the official website with documentation
	LINK_FRAGMENT_DOCS("link.fragment.docs"),

	// fragment link to the bot command reference, detailing all available commands and their usage
	LINK_FRAGMENT_COMMAND("link.fragment.command"),

	// fragment link to the error code details
	LINK_FRAGMENT_ERROR_CODE("link.fragment.error-code"),

	// represents the timeout duration (in milliseconds) for audio server connections
	AUDIO_SERVER_TIMEOUT_MS("audio.server.timeout-ms", Long::class),

	// represents the prefix used to search for content in audio server
	AUDIO_SERVER_SEARCH_DEFAULT_CONTENT_PREFIX("audio.server.search.default-content-prefix"),

	// extended link for radio playback content
	RADIO_PLAYBACK_EXTENDED_LINK("radio.playback.extended-link"),

	// JWizard API service host url.
	SERVICE_API_URL("service.api-url"),

	// JWizard front-end service host url
	SERVICE_FRONT_URL("service.front-url"),

	// discord api base path
	SERVICE_DISCORD_API("service.discord-api"),
	;
}
