/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.exception.spi

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import pl.jwizard.jwc.core.exception.CommandPipelineException
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext

/**
 * Interface for managing the creation of trackers for command pipeline exceptions, which may include messages or
 * interactive elements like links.
 *
 * @author Miłosz Gilga
 */
interface ExceptionTrackerStore {

	/**
	 * Initializes the internal trackers used for monitoring exceptions. This method sets up any necessary state or
	 * resources required for exception tracking.
	 */
	fun initTrackers()

	/**
	 * Creates a message embed to represent a specific exception. The message is generated based on an
	 * internationalization source, optional variables, and an optional command context.
	 *
	 * @param i18nSource the source of internationalized exception messages
	 * @param variables a map of variables used to populate the exception message (default is an empty map)
	 * @param context the context of the command when the exception occurred (optional)
	 * @return a message embed containing the details of the exception
	 */
	fun createTrackerMessage(
		i18nSource: I18nExceptionSource,
		variables: Map<String, Any?> = emptyMap(),
		context: CommandBaseContext? = null
	): MessageEmbed

	/**
	 * Creates a message embed based on an exception from the command pipeline. This overload directly takes a
	 * [CommandPipelineException] instance and generates the appropriate message.
	 *
	 * @param ex the command pipeline exception instance
	 * @return a message embed containing the details of the exception
	 */
	fun createTrackerMessage(ex: CommandPipelineException): MessageEmbed

	/**
	 * Creates an interactive action row (link) for a tracker based on an exception. The link is generated using an
	 * internationalization source and optional command context.
	 *
	 * @param i18nSource the source of internationalized exception messages
	 * @param context the context of the command when the exception occurred (optional)
	 * @return an action row containing a link for tracking the exception
	 */
	fun createTrackerLink(i18nSource: I18nExceptionSource, context: CommandBaseContext? = null): ActionRow

	/**
	 * Creates an interactive action row (link) based on an exception from the command pipeline. This overload directly
	 * takes a [CommandPipelineException] instance and generates the link.
	 *
	 * @param ex the command pipeline exception instance
	 * @return an action row containing a link for tracking the exception
	 */
	fun createTrackerLink(ex: CommandPipelineException): ActionRow
}
