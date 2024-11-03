/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

import pl.jwizard.jwc.core.DiscordBotAppRunner

/**
 * Interface representing a mechanism to load command classes dynamically in the Discord bot framework.
 *
 * Implementations of this interface are responsible for finding and registering command classes from the classpath,
 * allowing for modular command handling.
 *
 * @author Miłosz Gilga
 * @see DiscordBotAppRunner
 */
interface CommandsLoader {

	/**
	 * Loads classes via the reflection API. Implementations should use reflection to dynamically load and initialize
	 * classes related to commands.
	 */
	fun loadClassesViaReflectionApi()
}
