/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.stereotype

import pl.jwizard.jwc.core.DiscordBotAppRunner

/**
 * Interface for loading and managing commands metadata and integrity. This interface provides methods for loading
 * metadata, checking the integrity of the commands, and loading classes using reflection. Applied in command loader
 * in `jwc-command` submodule.
 *
 * @author Miłosz Gilga
 *
 */

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
	 * Checks the integrity of the loaded commands. Implementations should verify the consistency and correctness of
	 * commands including external and in-app commands definitions and their metadata. This method will be used to
	 * ensure that the commands are valid and can be safely used in the application.
	 */
	fun checkIntegrity()

	/**
	 * Loads classes via the reflection API. Implementations should use reflection to dynamically load and initialize
	 * classes related to commands.
	 */
	fun loadClassesViaReflectionApi()
}
