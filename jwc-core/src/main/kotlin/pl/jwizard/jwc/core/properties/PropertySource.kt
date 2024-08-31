/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.properties

/**
 * Bot properties source definitions. Defining places where properties should be loaded in hierarchical order.
 *
 * @see BotProperty
 * @author Miłosz Gilga
 */
enum class PropertySource {

	/**
	 * Property loading from properties file localized in resources classpath directory.
	 */
	FILE,

	/**
	 * Property defining as Vault KV store secret key and loading from Vault.
	 */
	VAULT,

	/**
	 * Property defining as command line argument and loading from startup parameters. Property must start with -D, ex.
	 * -Druntime.mode=dev
	 */
	ARG,
	;
}
