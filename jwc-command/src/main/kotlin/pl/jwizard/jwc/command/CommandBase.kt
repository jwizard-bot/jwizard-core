/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import pl.jwizard.jwc.command.event.CommandResponse
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.interaction.component.Paginator
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder

/**
 * Base class for commands that provides common functionalities.
 *
 * @property commandEnvironmentBean The environment dependencies required for command execution.
 * @author Miłosz Gilga
 */
abstract class CommandBase(protected val commandEnvironmentBean: CommandEnvironmentBean) {

	/**
	 * Creates a message embed builder configured with the given command context.
	 *
	 * @param context The context of the command execution.
	 * @return A configured MessageEmbedBuilder instance.
	 */
	protected fun createEmbedMessage(context: CommandContext) =
		MessageEmbedBuilder(context, commandEnvironmentBean.i18nBean, commandEnvironmentBean.jdaColorStoreBean)

	/**
	 * Creates a paginator for displaying multiple pages of content.
	 *
	 * @param context The context of the command execution.
	 * @param pages The list of pages to be included in the paginator.
	 * @return A Paginator instance that handles pagination logic.
	 */
	protected fun createPaginator(context: CommandContext, pages: List<String>) = Paginator(
		context,
		i18nBean = commandEnvironmentBean.i18nBean,
		eventQueueBean = commandEnvironmentBean.eventQueueBean,
		jdaColorStoreBean = commandEnvironmentBean.jdaColorStoreBean,
		pages
	)

	/**
	 * Executes the command logic and returns the command response.
	 *
	 * @param context The context of the command execution.
	 * @return A CommandResponse object containing the results of the command execution.
	 */
	abstract fun execute(context: CommandContext): CommandResponse
}
