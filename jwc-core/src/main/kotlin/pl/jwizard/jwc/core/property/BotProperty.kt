/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import pl.jwizard.jwl.property.AppProperty
import kotlin.reflect.KClass

/**
 * Enum class representing configuration properties. This serves as a single source of truth for property keys and
 * their types.
 *
 * Defining following properties:
 * - [JDA_SECRET_TOKEN]: JDA secret token.
 * - [JDA_DEFAULT_ACTIVITY]: JDA default activity. Enabled when [JDA_SPLASHES_ENABLED] property is set to false.
 * - [JDA_SPLASHES_ENABLED]: JDA splashes toggle boolean property. If true, splashes are enabled, otherwise show nothing.
 * - [JDA_SPLASHES_INTERVAL_SEC]: JDA splashes interval in seconds.
 * - [JDA_COLOR_PRIMARY]: JDA primary color in embed messages.
 * - [JDA_COLOR_DANGER]: JDA danger color in embed messages.
 * - [JDA_INTERACTION_MESSAGE_MAX_EMBEDS]: Maximum embeds messages in single JDA command interaction.
 * - [JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_ROWS]: Maximum action rows in single JDA command message with custom
 *   interaction.
 * - [JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_COMPONENTS_IN_ROW]: Maximum components in single action row in single JDA
 *   command message with custom interaction.
 * - [JDA_INTERACTION_MESSAGE_COMPONENT_DISABLE_DELAY_SEC]: The delay time (in seconds) before disabling interaction
 *   components in JDA.
 * - [JDA_INTERACTION_SLASH_AUTOCOMPLETE_MAX_OPTIONS]: JDA maximum number of options in single autocomplete interaction
 *   request.
 * - [JDA_EXCEPTION_SEGMENT_SIZE]: Specifies the maximum size of the segment in exception group.
 * - [JDA_EXCEPTION_URL_REFER_TEMPLATE]: Defines the URL template used for referring to detailed exception information
 *   in bot website.
 * - [JDA_PAGINATION_CHUNK_SIZE]: Defines chunk size for custom paginator for embed messages in command handlers.
 * - [LINK_WEBSITE]: The official website for the bot. It contains general information and features.
 * - [LINK_REPOSITORY]: The repository URL where the bot's source code is hosted.
 * - [LINK_DOCS]: The official website with documentation.
 * - [LINK_COMMAND]: Direct link to the bot command reference, detailing all available commands and their usage.
 * - [LAVALINK_TIMEOUT_MS]: Represents the timeout duration (in milliseconds) for Lavalink connections.
 * - [LAVALINK_SEARCH_CONTENT_PREFIX]: Represents the prefix used to search for content in Lavalink.
 * - [RADIO_PLAYBACK_EXTENDED_LINK]: Extended link for radio playback content.
 * - [SERVICE_API_URL]: JWizard API service host URL.
 * - [SERVICE_FRONT_URL]: JWizard front-end service host url.
 *
 * @property key The key used to retrieve the property value from various property sources.
 * @property type The type of the property value. Defaults to [String] if not specified.
 * @author Miłosz Gilga
 * @see BotListProperty
 */
enum class BotProperty(
	override val key: String,
	override val type: KClass<*> = String::class,
) : AppProperty {

	/**
	 * JDA secret token.
	 */
	JDA_SECRET_TOKEN("jda.secret-token"),

	/**
	 * JDA default activity. Enabled when [JDA_SPLASHES_ENABLED] property is set to false.
	 */
	JDA_DEFAULT_ACTIVITY("jda.default-activity"),

	/**
	 * JDA splashes toggle boolean property. If true, splashes are enabled, otherwise show nothing.
	 */
	JDA_SPLASHES_ENABLED("jda.splashes.enabled", Boolean::class),

	/**
	 * JDA splashes interval in seconds.
	 */
	JDA_SPLASHES_INTERVAL_SEC("jda.splashes.interval-sec", Long::class),

	/**
	 * JDA primary color in embed messages.
	 */
	JDA_COLOR_PRIMARY("jda.color.primary", Int::class),

	/**
	 * JDA danger color in embed messages.
	 */
	JDA_COLOR_DANGER("jda.color.danger", Int::class),

	/**
	 * Maximum embeds messages in single JDA command interaction.
	 */
	JDA_INTERACTION_MESSAGE_MAX_EMBEDS("jda.interaction.message.max-embeds", Int::class),

	/**
	 * Maximum action rows in single JDA command message with custom interaction.
	 */
	JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_ROWS("jda.interaction.message.action-row.max-rows", Int::class),

	/**
	 * Maximum components in single action row in single JDA command message with custom interaction.
	 */
	JDA_INTERACTION_MESSAGE_ACTION_ROW_MAX_COMPONENTS_IN_ROW(
		"jda.interaction.message.action-row.max-components-in-row",
		Int::class
	),

	/**
	 * The delay time (in seconds) before disabling interaction components in JDA.
	 */
	JDA_INTERACTION_MESSAGE_COMPONENT_DISABLE_DELAY_SEC(
		"jda.interaction.message.component.disable-delay-sec",
		Long::class
	),

	/**
	 * JDA maximum number of options in single autocomplete interaction request.
	 */
	JDA_INTERACTION_SLASH_AUTOCOMPLETE_MAX_OPTIONS("jda.interaction.slash.autocomplete.max-options", Int::class),

	/**
	 * Specifies the maximum size of the segment in exception group.
	 */
	JDA_EXCEPTION_SEGMENT_SIZE("jda.exception.segment-size", Int::class),

	/**
	 * Defines the URL template used for referring to detailed exception information in bot website.
	 */
	JDA_EXCEPTION_URL_REFER_TEMPLATE("jda.exception.url-refer-template"),

	/**
	 * Defines chunk size for custom paginator for embed messages in command handlers.
	 */
	JDA_PAGINATION_CHUNK_SIZE("jda.pagination.chunk-size", Int::class),

	/**
	 * The official website for the bot. It contains general information and features.
	 */
	LINK_WEBSITE("link.website"),

	/**
	 * The repository URL where the bot's source code is hosted.
	 */
	LINK_REPOSITORY("link.repository"),

	/**
	 * The official website with documentation.
	 */
	LINK_DOCS("link.docs"),

	/**
	 * Direct link to the bot command reference, detailing all available commands and their usage.
	 */
	LINK_COMMAND("link.command"),

	/**
	 * Represents the timeout duration (in milliseconds) for Lavalink connections.
	 */
	LAVALINK_TIMEOUT_MS("lavalink.timeout-ms", Long::class),

	/**
	 * Represents the prefix used to search for content in Lavalink.
	 */
	LAVALINK_SEARCH_CONTENT_PREFIX("lavalink.search.content-prefix"),

	/**
	 * Extended link for radio playback content.
	 */
	RADIO_PLAYBACK_EXTENDED_LINK("radio.playback.extended-link"),

	/**
	 * JWizard API service host url.
	 */
	SERVICE_API_URL("service.api-url"),

	/**
	 * JWizard front-end service host url.
	 */
	SERVICE_FRONT_URL("service.front-url"),

	/**
	 * JVM name property, indicating the name of the Java Virtual Machine.
	 */
	JVM_NAME("java.vm.name"),

	/**
	 * JRE version property, representing the version of the Java Runtime Environment.
	 */
	JRE_VERSION("java.runtime.version"),

	/**
	 * OS name property, defining the name of the operating system.
	 */
	OS_NAME("os.name"),

	/**
	 * OS architecture property, specifying the architecture of the operating system.
	 */
	OS_ARCHITECTURE("os.arch"),
	;
}
