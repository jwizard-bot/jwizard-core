/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util.ext

/**
 * Extension property for converting a camelCase string into dash-case (also known as kebab-case). This transformation
 * is useful when converting identifiers or keys from camelCase to a more URL-friendly or standardized format.
 *
 * It works by inserting a dash (`-`) between lowercase and uppercase letters and then converting the
 * entire string to lowercase.
 *
 * Example:
 * ```
 * "camelCaseExample".fromCamelToDashCase // returns "camel-case-example"
 * ```
 *
 * @return A new string converted from camelCase to dash-case.
 * @author Miłosz Gilga
 */
val String.fromCamelToDashCase get() = replace(Regex("([a-z])([A-Z]+)"), "$1-$2").lowercase()
