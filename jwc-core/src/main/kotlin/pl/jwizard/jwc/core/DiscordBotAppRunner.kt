/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import pl.jwizard.jwc.core.util.FancyTitlePrinter
import kotlin.reflect.KClass

/**
 * The main class that loads resources, configuration and runs the bot instance.
 * Use this class with [DiscordBotApp] annotation. Singleton instance.
 *
 * @author Miłosz Gilga
 */
object DiscordBotAppRunner {
	private val log = LoggerFactory.getLogger(DiscordBotAppRunner::class.java)

	/**
	 * Base application package. Used for Spring Context [ComponentScan] annotation. All classes related with
	 * Spring IoC containers will be loaded into Spring Context.
	 */
	const val BASE_PACKAGE = "pl.jwizard"

	/**
	 * Fancy title banner classpath location in `resources` directory
	 */
	private const val BANNER_CLASSPATH_LOCATION = "/util/banner.txt"

	/**
	 * Fancy frame elements rendered together with fancy title banner element.
	 */
	private val FANCY_FRAME_ELEMENTS = listOf(
		"Source code repository: https://github.com/jwizard-bot/jwizard-core",
		"Originally developed by: Miłosz Gilga (https://miloszgilga.pl)",
		"Application license you will find in the LICENSE file",
	)

	/**
	 * Spring Context instance.
	 */
	private lateinit var context: ApplicationContext

	/**
	 * Static method which starts loading configuration, resources and a new JDA instance of the bot being created. By
	 * default, loads variables from root .env file.
	 *
	 * @param args  command line args, passing from main method, accept only with *jda* prefix, ex. *jda.name*,
	 * @param clazz main type of class that runs the bot.
	 * @author Miłosz Gilga
	 */
	fun run(args: Array<String>, clazz: KClass<*>) {
		run(args, clazz, true)
	}

	/**
	 * Static method which starts loading configuration, resources and a new JDA instance of the bot being created.
	 *
	 * @param args  command line args, passing from main method, accept only with *jda* prefix, ex. *jda.name*,
	 * @param clazz main type of class that runs the bot.
	 * @param envFileLoader determinate, if application should load variables from root .env file
	 * @author Miłosz Gilga
	 */
	fun run(args: Array<String>, clazz: KClass<*>, envFileLoader: Boolean) {
		val fancyTitlePrinter = FancyTitlePrinter(
			fileClasspathLocation = BANNER_CLASSPATH_LOCATION,
			frameElements = FANCY_FRAME_ELEMENTS,
		)
		fancyTitlePrinter.printTitle()
		fancyTitlePrinter.printFrame()

		

		context = AnnotationConfigApplicationContext(clazz.java)
		log.info("Init Spring Context with base class: {}. Init packages tree: {}.", clazz, BASE_PACKAGE)
	}
}
