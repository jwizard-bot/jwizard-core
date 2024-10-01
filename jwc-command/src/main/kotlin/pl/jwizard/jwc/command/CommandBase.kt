/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.interaction.component.Paginator
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import java.util.concurrent.CompletableFuture

/**
 * Base class for commands that provides common functionalities.
 *
 * @property commandEnvironment The environment dependencies required for command execution.
 * @author Miłosz Gilga
 */
abstract class CommandBase(private val commandEnvironment: CommandEnvironmentBean) {

	protected val environmentBean = commandEnvironment.environmentBean
	protected val guildSettingsEventAction = commandEnvironment.guildSettingsEventAction
	protected val i18nBean = commandEnvironment.i18nBean
	protected val radioStationSupplier = commandEnvironment.radioStationSupplier
	protected val jdaInstance = commandEnvironment.jdaInstance

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
	protected fun createPaginator(context: CommandContext, pages: List<String>) = Paginator(
		context,
		i18nBean,
		eventQueueBean = commandEnvironment.eventQueueBean,
		jdaColorStoreBean = commandEnvironment.jdaColorStoreBean,
		pages
	)

	/**
	 * Executes the command logic and returns the command response.
	 *
	 * @param context The context of the command execution.
	 * @param response
	 */
	abstract fun execute(context: CommandContext, response: CompletableFuture<CommandResponse>)
}
