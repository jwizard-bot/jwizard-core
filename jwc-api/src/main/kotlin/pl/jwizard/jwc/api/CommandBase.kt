package pl.jwizard.jwc.api

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.buttons.Button
import pl.jwizard.jwc.command.GuildCommandHandler
import pl.jwizard.jwc.command.interaction.component.Paginator
import pl.jwizard.jwc.command.interaction.component.RefreshableComponent
import pl.jwizard.jwc.core.i18n.source.I18nActionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.LinkFragmentProperty
import pl.jwizard.jwl.property.AppProperty

internal abstract class CommandBase(
	protected val commandEnvironment: CommandEnvironment,
) : GuildCommandHandler {
	protected val environment = commandEnvironment.environment
	protected val guildEnvironment = commandEnvironment.guildEnvironment
	protected val guildSettingsEventAction = commandEnvironment.guildSettingsEventAction
	protected val i18n = commandEnvironment.i18n
	protected val jdaShardManager = commandEnvironment.jdaShardManager
	protected val exceptionTrackerHandler = commandEnvironment.exceptionTrackerHandler
	protected val botEmojisCache = commandEnvironment.botEmojisCache

	protected val superuserPermissions = environment
		.getListProperty<String>(BotListProperty.JDA_SUPERUSER_PERMISSIONS)

	protected fun createEmbedMessage(
		context: CommandBaseContext,
	) = MessageEmbedBuilder(i18n, commandEnvironment.jdaColorStore, context)

	protected fun createPaginator(
		context: CommandBaseContext,
		pages: List<MessageEmbed>
	) = Paginator(
		context,
		pages,
		i18n,
		commandEnvironment.eventQueue,
		botEmojisCache,
	)

	protected fun createRefreshable(
		onRefresh: (response: MutableList<MessageEmbed>) -> Unit,
	) = RefreshableComponent(i18n, commandEnvironment.eventQueue, botEmojisCache, onRefresh)

	protected fun createLinkFromFragment(
		linkFragmentProperty: LinkFragmentProperty,
		vararg args: Any?,
	): String {
		val baseUrl = environment.getProperty<String>(BotProperty.LINK_WEBSITE)
		val fragmentUrl = environment.getProperty<String>(linkFragmentProperty)
		return fragmentUrl.format(baseUrl, *args)
	}

	protected fun createLinkButton(
		name: I18nActionSource,
		link: AppProperty,
		context: CommandBaseContext,
	) = Button.link(environment.getProperty<String>(link), i18n.t(name, context.language))
}
