/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.exception

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext

/**
 * Interface defining a structure for handling exceptions related to the command pipeline.
 *
 * @author Miłosz Gilga
 */
interface CommandPipelineException {

	/**
	 * The context of the base command when the exception occurred. This might be null if the context is not applicable
	 * or unavailable.
	 */
	val commandBaseContext: CommandBaseContext?

	/**
	 * Internationalization (i18n) source providing localized messages for the exception.
	 */
	val i18nExceptionSource: I18nExceptionSource

	/**
	 * A map containing additional variables that can be used to populate exception messages or logs.
	 */
	val variables: Map<String, Any?>

	/**
	 * The log message associated with the exception, used for debugging or tracking purposes. This may be null if no
	 * specific log message is available.
	 */
	val logMessage: String?
}
