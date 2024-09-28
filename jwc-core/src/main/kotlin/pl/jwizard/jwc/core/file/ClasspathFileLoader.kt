/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.file

import org.springframework.core.io.ClassPathResource
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets

/**
 * Class responsible for loading, reading and handling classpath files, including reading raw content and checking
 * other major file parameters.
 *
 * @property location Classpath file location ex. `static/example.txt`.
 * @author Miłosz Gilga
 */
class ClasspathFileLoader(private val location: String) {

	/**
	 * File representation by [ClassPathResource] hook java class.
	 */
	private val classPathResource: ClassPathResource = ClassPathResource(location)

	/**
	 * Method responsible for reading raw file content from declared classpath [location]. If file not exist,
	 * throw [FileNotFoundException].
	 *
	 * @return raw file content if file exist. Otherwise, null.
	 * @author Miłosz Gilga
	 */
	fun readFileRaw(): String {
		return classPathResource.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
	}
}
