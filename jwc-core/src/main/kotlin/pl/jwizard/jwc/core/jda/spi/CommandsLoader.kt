/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

import pl.jwizard.jwc.core.DiscordBotAppRunner

/**
 * Interface for loading and managing commands metadata and integrity. This interface defines the contract for
 * operations related to command metadata, integrity checking, and class loading. Implementations of this interface
 * will be provided in separate modules and registered as Spring beans for dependency injection.
 *
 * Modules that depend on this interface (current module) can work with command-related functionality
 * without knowing the details of the implementations (provided by `jwc-command` module).
 *
 * @author Miłosz Gilga
 * @see DiscordBotAppRunner
 */
interface CommandsLoader {

	/**
	 * Loads the metadata related to commands. Implementations of this interface should provide the logic to retrieve
	 * and process command metadata. This method will be called by the Spring container as needed.
	 */
	fun loadMetadata()

	/**
	 * Loads classes via the reflection API. Implementations should use reflection to dynamically load and initialize
	 * classes related to commands.
	 */
	fun loadClassesViaReflectionApi()
}