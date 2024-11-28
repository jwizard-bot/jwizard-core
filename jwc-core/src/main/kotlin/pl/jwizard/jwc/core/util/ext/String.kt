/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util.ext

/**
 * Formats the current string as a version string by prepending "v" to it. This is useful for displaying version
 * numbers in a standard format.
 *
 * @return The version string with a "v" prefix.
 * @author Miłosz Gilga
 */
val String.versionFormat get() = "v$this"
