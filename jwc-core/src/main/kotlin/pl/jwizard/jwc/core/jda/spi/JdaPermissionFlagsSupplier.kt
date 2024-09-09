/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

/**
 * Interface for providing permission flags used in JDA (Java Discord API) operations.
 *
 * This interface defines a method for retrieving a list of permission flags. Implementations of this interface are
 * expected to provide the logic for fetching the flags, which might come from various sources, such as a database
 * or configuration file.
 *
 * @author Miłosz Gilga
 */
interface JdaPermissionFlagsSupplier {

	/**
	 * Retrieves a list of permission flags.
	 *
	 * Implementations of this method should provide the logic for fetching the permission flags. The flags are returned
	 * as a list of strings, where each string represents a permission flag.
	 *
	 * @return A list of permission flag names.
	 */
	fun getPermissionFlags(): List<String>
}
