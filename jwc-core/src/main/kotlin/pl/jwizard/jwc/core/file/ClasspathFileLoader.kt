/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.file

import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * Class responsible for loading, reading and handling classpath files, including reading raw content and checking
 * other major file parameters.
 *
 * @property location classpath file location, starts with `/`, ex. `/static/example.txt`.
 * @author Miłosz Gilga
 */
class ClasspathFileLoader(private val location: String) : AutoCloseable {

	/**
	 * File representation by [InputStream] hook java class.
	 */
	private var inputStream: InputStream? = null

	init {
		inputStream = javaClass.getResourceAsStream(location)
	}

	/**
	 * Method responsible for reading raw file content from declared classpath [location]. If file not exist,
	 * return null.
	 *
	 * @return raw file content if file exist. Otherwise, null.
	 * @author Miłosz Gilga
	 */
	fun readFileRaw(): String? {
		return inputStream?.let { stream -> stream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() } }
	}

	/**
	 * Check if passed file with classpath location exists and it's available.
	 *
	 * @return true if file exist, otherwise false
	 * @author Miłosz Gilga
	 */
	fun fileExist(): Boolean = inputStream != null

	override fun close() {
		inputStream?.close()
	}
}
