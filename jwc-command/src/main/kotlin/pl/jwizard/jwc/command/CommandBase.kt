/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.interaction.component.Paginator
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.BotListProperty

/**
 * Base class for commands that provides common functionalities.
 *
 * @property commandEnvironment The environment dependencies required for command execution.
 * @author Miłosz Gilga
 */
abstract class CommandBase(protected val commandEnvironment: CommandEnvironmentBean) {

	protected val environmentBean = commandEnvironment.environmentBean
	protected val guildSettingsEventAction = commandEnvironment.guildSettingsEventAction
	protected val i18nBean = commandEnvironment.i18nBean
	protected val radioStationSupplier = commandEnvironment.radioStationSupplier
	protected val jdaInstance = commandEnvironment.jdaInstance
	protected val eventQueueBean = commandEnvironment.eventQueueBean
	protected val commandDataSupplier = commandEnvironment.commandDataSupplier
	protected val commandsCacheBean = commandEnvironment.commandsCacheBean
	protected val commandHelpMessageBean = commandEnvironment.commandHelpMessageBean

	/**
	 * A list of permissions assigned to superusers.
	 *
	 * @see BotListProperty.JDA_SUPERUSER_PERMISSIONS
	 */
	protected val superuserPermissions =
		environmentBean.getListProperty<String>(BotListProperty.JDA_SUPERUSER_PERMISSIONS)

	/**
	 * Creates a message embed builder configured with the given command context.
	 *
	 * @param context The context of the command execution.
	 * @return A configured MessageEmbedBuilder instance.
	 */
	protected fun createEmbedMessage(context: CommandContext) =
		MessageEmbedBuilder(commandEnvironment.i18nBean, commandEnvironment.jdaColorStoreBean, context)

	/**
	 * Creates a paginator for displaying multiple pages of content.
	 *
	 * @param context The context of the command execution.
	 * @param pages The list of pages to be included in the paginator.
	 * @return A Paginator instance that handles pagination logic.
	 */
	protected fun createPaginator(context: CommandContext, pages: List<MessageEmbed>) = Paginator(
		context,
		i18nBean,
		eventQueueBean = commandEnvironment.eventQueueBean,
		jdaColorStoreBean = commandEnvironment.jdaColorStoreBean,
		pages,
	)

	/**
	 * Executes the command logic and returns the command response.
	 *
	 * @param context The context of the command execution.
	 * @param response
	 */
	abstract fun execute(context: CommandContext, response: TFutureResponse)
}
