/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import org.springframework.context.support.AbstractMessageSource
import pl.jwizard.jwc.core.property.BotProperty.*
import kotlin.reflect.KClass

/**
 * Enum class representing configuration properties. This serves as a single source of truth for property keys and
 * their types.
 *
 * Defining following properties:
 *
 * - [ENV_ENABLED]: Determines if the application should load environment variables from a .env file at startup.
 * - [DEPLOYMENT_BUILD_VERSION]: Deployment build version. Generated by CI/CD pipeline. Default value: *UNKNOWN*.
 * - [DEPLOYMENT_LAST_BUILD_DATE]: Deployment last build date. Generated by CI/CD pipeline. Default value: *UNKNOWN*.
 * - [DB_URL]: Database JDBC URL provider.
 * - [DB_USERNAME]: Database username.
 * - [DB_PASSWORD]: Database password.
 * - [DB_DRIVER_CLASS_NAME]: Database driver class name (full qualified path).
 * - [VAULT_URL]: Vault key storage URL.
 * - [VAULT_TOKEN]: Vault key storage access token.
 * - [VAULT_KV_BACKEND]: Vault key storage KV backend name.
 * - [VAULT_KV_DEFAULT_CONTEXT]: Vault key storage KV default context. Load default secrets independently of
 * 	 application name.
 * - [VAULT_KV_APPLICATION_NAME]: Vault key storage KV application name. Load all secrets from this pre-path.
 * - [JDA_NAME]: JDA instance name.
 * - [JDA_SECRET_TOKEN]: JDA secret token.
 * - [JDA_DEFAULT_ACTIVITY]: JDA default activity. Enabled when [JDA_SPLASHES_ENABLED] property is set to false.
 * - [JDA_PAGINATION_MAX_ELEMENTS_PER_PAGE]: Max elements per page number for JDA pagination generator.
 * - [JDA_PAGINATION_MENU_ALIVE_SEC]: JDA pagination generator menu visibility in seconds.
 * - [JDA_SPLASHES_ENABLED]: JDA splashes toggle boolean property. If true, splashes are enabled, otherwise show nothing.
 * - [JDA_SPLASHES_INTERVAL_SEC]: JDA splashes interval in seconds.
 * - [JDA_COLOR_PRIMARY]: JDA primary color in embed messages.
 * - [JDA_COLOR_SECONDARY]: JDA secondary color in embed messages.
 * - [JDA_COLOR_TINT]: JDA tint color in embed messages.
 * - [JDA_COLOR_DANGER]: JDA danger color in embed messages.
 * - [JDA_INTERACTION_MAX_EMBED_MESSAGES]: Maximum embeds messages in single JDA command interaction.
 * - [JDA_INTERACTION_MAX_ACTION_ROWS]: Maximum action rows in single JDA command message with custom interaction.
 * - [JDA_INTERACTION_MAX_COMPONENTS_IN_ACTION_ROW]: Maximum components in single action row in single JDA command
 *   message with custom interaction.
 * - [JDA_INTERACTION_DISABLE_COMPONENTS_DELAY_SEC]: The delay time (in seconds) before disabling interaction
 *   components in JDA.
 * - [JDA_EXCEPTION_SEGMENT_SIZE]: Specifies the maximum size of the segment in exception group.
 * - [JDA_EXCEPTION_URL_REFER_TEMPLATE]: Defines the URL template used for referring to detailed exception information
 *   in bot website.
 * - [SERVICE_API_URL]: JWizard API service host URL.
 * - [SERVICE_FRONT_URL]: JWizard front-end service host url.
 * - [S3_PUBLIC_API_URL]: The URL or endpoint for accessing the public S3 API.
 * - [S3_HOST_URL]: The host address of the S3 service.
 * - [S3_REGION]: The region where the S3 bucket is hosted.
 * - [S3_ACCESS_KEY]: The access key for S3 authentication.
 * - [S3_SECRET_KEY]: The secret key for S3 authentication.
 * - [S3_ROOT_BUCKET]: The name of the root bucket in the S3 storage.
 * - [S3_PATH_STYLE_ACCESS_ENABLED]: Determines if path-style access is enabled for the S3 service.
 * - [I18N_DEFAULT_LANGUAGE]: I18n default language (as language tag, without localization property).
 * - [I81N_REVALIDATE_CACHE_SEC]: I18n revalidate cache time interval in seconds. For non-positive value (including *0*,
 *   ex. *-1*) never revalidate cache. For more info, check `setCacheSeconds` method in [AbstractMessageSource] class.
 * - [GUILD_VOTING_PERCENTAGE_RATIO]: Ratio of voting percentage for guilds.
 * - [GUILD_TIME_TO_FINISH_VOTING_SEC]: Maximum voting time for guilds in seconds.
 * - [GUILD_DJ_ROLE_NAME]: Name of the DJ role in guilds.
 * - [GUILD_MAX_REPEATS_OF_TRACK]: Maximum number of repeats allowed for a track in guilds.
 * - [GUILD_LEAVE_EMPTY_CHANNEL_SEC]: Time in seconds after which the bot leaves an empty channel in guilds.
 * - [GUILD_LEAVE_NO_TRACKS_SEC]: Time in seconds after which the bot leaves a channel with no tracks in guilds.
 * - [GUILD_DEFAULT_VOLUME]: Default volume level for guilds.
 * - [GUILD_RANDOM_AUTO_CHOOSE_TRACK]: Indicates whether to randomly auto-choose tracks in guilds.
 * - [GUILD_TIME_AFTER_AUTO_CHOOSE_SEC]: Time in seconds after which the bot automatically chooses a track in guilds.
 * - [GUILD_MAX_TRACKS_TO_CHOOSE]: Maximum number of tracks to choose from in guilds.
 * - [GUILD_DEFAULT_LEGACY_PREFIX]: Default legacy command prefix used in guild.
 * - [GUILD_DEFAULT_SLASH_ENABLED]: Determines if the slash command system is enabled by default in guild.
 *
 * @property key The key used to retrieve the property value from various property sources.
 * @property type The type of the property value. Defaults to [String] if not specified.
 * @author Miłosz Gilga
 * @see BotListProperty
 */
enum class BotProperty(
	override val key: String,
	val type: KClass<*> = String::class,
) : Property {

	/**
	 * Determinate, if application at start should load environment variables from .env file.
	 */
	ENV_ENABLED("env.enabled", Boolean::class),

	/**
	 * Deployment build version. Generated by CI/CD pipeline. Default value: *UNKNOWN*.
	 */
	DEPLOYMENT_BUILD_VERSION("deployment.build-version"),

	/**
	 * Deployment last build date. Generated by CI/CD pipeline. Default value: *UNKNOWN*.
	 */
	DEPLOYMENT_LAST_BUILD_DATE("deployment.last-build-date"),

	/**
	 * Database JDBC url provider.
	 */
	DB_URL("db.jdbc"),

	/**
	 * Database username.
	 */
	DB_USERNAME("db.username"),

	/**
	 * Database password.
	 */
	DB_PASSWORD("db.password"),

	/**
	 * Database driver class name (full qualified path).
	 */
	DB_DRIVER_CLASS_NAME("db.driver-class-name"),

	/**
	 * Vault key storage url.
	 */
	VAULT_URL("vault.url"),

	/**
	 * Vault key storage access token.
	 */
	VAULT_TOKEN("vault.token"),

	/**
	 * Vault key storage KV backend name.
	 */
	VAULT_KV_BACKEND("vault.kv.backend"),

	/**
	 * Vault key storage KV default context. Load default secrets independently of application name.
	 */
	VAULT_KV_DEFAULT_CONTEXT("vault.kv.default-context"),

	/**
	 * Vault key storage KV application name. Load all secrets from this pre-path.
	 */
	VAULT_KV_APPLICATION_NAME("vault.kv.application-name"),

	/**
	 * JDA instance name.
	 */
	JDA_NAME("jda.name"),

	/**
	 * JDA secret token.
	 */
	JDA_SECRET_TOKEN("jda.secret-token"),

	/**
	 * JDA default activity. Enabled when [JDA_SPLASHES_ENABLED] property is set to false.
	 */
	JDA_DEFAULT_ACTIVITY("jda.default-activity"),

	/**
	 * Max elements per page number for JDA pagination generator.
	 */
	JDA_PAGINATION_MAX_ELEMENTS_PER_PAGE("jda.pagination.max-elements-per-page", Int::class),

	/**
	 * JDA pagination generator menu visibility in seconds.
	 */
	JDA_PAGINATION_MENU_ALIVE_SEC("jda.pagination.menu-alive-sec", Int::class),

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
	 * JDA secondary color in embed messages.
	 */
	JDA_COLOR_SECONDARY("jda.color.secondary", Int::class),

	/**
	 * JDA tint color in embed messages.
	 */
	JDA_COLOR_TINT("jda.color.tint", Int::class),

	/**
	 * JDA danger color in embed messages.
	 */
	JDA_COLOR_DANGER("jda.color.danger", Int::class),

	/**
	 * Maximum embeds messages in single JDA command interaction.
	 */
	JDA_INTERACTION_MAX_EMBED_MESSAGES("jda.interaction.max-embed-messages", Int::class),

	/**
	 * Maximum action rows in single JDA command message with custom interaction.
	 */
	JDA_INTERACTION_MAX_ACTION_ROWS("jda.interaction.max-embed-messages", Int::class),

	/**
	 * Maximum components in single action row in single JDA command message with custom interaction.
	 */
	JDA_INTERACTION_MAX_COMPONENTS_IN_ACTION_ROW("jda.interaction.max-components-in-action-row", Int::class),

	/**
	 * The delay time (in seconds) before disabling interaction components in JDA.
	 */
	JDA_INTERACTION_DISABLE_COMPONENTS_DELAY_SEC("jda.interaction.disable-components-delay-sec", Long::class),

	/**
	 * Specifies the maximum size of the segment in exception group.
	 */
	JDA_EXCEPTION_SEGMENT_SIZE("jda.exception.segment-size", Int::class),

	/**
	 * Defines the URL template used for referring to detailed exception information in bot website.
	 */
	JDA_EXCEPTION_URL_REFER_TEMPLATE("jda.exception.url-refer-template"),

	/**
	 * JWizard API service host url.
	 */
	SERVICE_API_URL("service.api-url"),

	/**
	 * JWizard front-end service host url.
	 */
	SERVICE_FRONT_URL("service.front-url"),

	/**
	 * The URL or endpoint for accessing the public S3 API.
	 */
	S3_PUBLIC_API_URL("s3.public-api-url"),

	/**
	 * The host address of the S3 service.
	 */
	S3_HOST_URL("s3.host-url"),

	/**
	 * The region where the S3 bucket is hosted.
	 */
	S3_REGION("s3.region"),

	/**
	 * The access key for S3 authentication.
	 */
	S3_ACCESS_KEY("s3.access-key"),

	/**
	 * The secret key for S3 authentication.
	 */
	S3_SECRET_KEY("s3.secret-key"),

	/**
	 * The name of the root bucket in the S3 storage.
	 */
	S3_ROOT_BUCKET("s3.root-bucket"),

	/**
	 * Determines if path-style access is enabled for the S3 service.
	 */
	S3_PATH_STYLE_ACCESS_ENABLED("s3.path-style-access-enabled", Boolean::class),

	/**
	 * I18n default language (as language tag, without localization property).
	 */
	I18N_DEFAULT_LANGUAGE("i18n.default-language"),

	/**
	 * I18n revalidate cache time interval in seconds. For non-positive value (including *0*, ex. *-1*) never revalidate
	 * cache. For more info, check `setCacheSeconds()` method in [AbstractMessageSource] class
	 */
	I81N_REVALIDATE_CACHE_SEC("i18n.revalidate-cache-sec", Int::class),

	/**
	 * JVM name property, indicating the name of the Java Virtual Machine.
	 */
	JVM_NAME("java.vm.name"),

	/**
	 * JRE name property, specifying the name of the Java Runtime Environment.
	 */
	JRE_NAME("java.runtime.name"),

	/**
	 * JRE version property, representing the version of the Java Runtime Environment.
	 */
	JRE_VERSION("java.runtime.version"),

	/**
	 * JRE specification version property, indicating the version of the Java specification.
	 */
	JRE_SPEC_VERSION("java.specification.version"),

	/**
	 * OS name property, defining the name of the operating system.
	 */
	OS_NAME("os.name"),

	/**
	 * OS architecture property, specifying the architecture of the operating system.
	 */
	OS_ARCHITECTURE("os.arch"),

	/**
	 * Ratio of voting percentage for guilds.
	 */
	GUILD_VOTING_PERCENTAGE_RATIO("guild.voting-percentage-ratio", Int::class),

	/**
	 * Maximum voting time for guilds in seconds.
	 */
	GUILD_TIME_TO_FINISH_VOTING_SEC("guild.time-to-finish-voting-sec", Int::class),

	/**
	 * Name of the DJ role in guilds.
	 */
	GUILD_DJ_ROLE_NAME("guild.dj-role-name"),

	/**
	 * Maximum number of repeats allowed for a track in guilds.
	 */
	GUILD_MAX_REPEATS_OF_TRACK("guild.max-repeats-of-track", Int::class),

	/**
	 * Time in seconds after which the bot leaves an empty channel in guilds.
	 */
	GUILD_LEAVE_EMPTY_CHANNEL_SEC("guild.leave-empty-channel-sec", Int::class),

	/**
	 * Time in seconds after which the bot leaves a channel with no tracks in guilds.
	 */
	GUILD_LEAVE_NO_TRACKS_SEC("guild.leave-no-tracks-channel-sec", Int::class),

	/**
	 * Default volume level for guilds.
	 */
	GUILD_DEFAULT_VOLUME("guild.default-volume", Int::class),

	/**
	 * Indicates whether to randomly auto-choose tracks in guilds.
	 */
	GUILD_RANDOM_AUTO_CHOOSE_TRACK("guild.random-auto-choose-track", Boolean::class),

	/**
	 * Time in seconds after which the bot automatically chooses a track in guilds.
	 */
	GUILD_TIME_AFTER_AUTO_CHOOSE_SEC("guild.time-after-auto-choose-sec", Int::class),

	/**
	 * Maximum number of tracks to choose from in guilds.
	 */
	GUILD_MAX_TRACKS_TO_CHOOSE("guild.tracks-to-choose-max", Int::class),

	/**
	 * Default legacy command prefix used in guild.
	 */
	GUILD_DEFAULT_LEGACY_PREFIX("guild.default-legacy-prefix"),

	/**
	 * Determines if the slash command system is enabled by default in guild.
	 */
	GUILD_DEFAULT_SLASH_ENABLED("guild.default-slash-enabled", Boolean::class)
	;
}
