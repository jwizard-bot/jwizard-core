/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core

import org.slf4j.LoggerFactory
import org.springframework.beans.FatalBeanException
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import pl.jwizard.jwc.core.printer.ConsolePrinter
import pl.jwizard.jwc.core.printer.FancyFramePrinter
import pl.jwizard.jwc.core.printer.FancyTitlePrinter
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
	 * Fancy title banner classpath location in `resources` directory.
	 */
	private const val BANNER_CLASSPATH_LOCATION = "util/banner.txt"

	/**
	 * Fancy frame classpath location in `resources` directory.
	 */
	private const val FRAME_CLASSPATH_LOCATION = "util/frame.txt"

	/**
	 * Spring Context instance.
	 */
	private lateinit var context: ApplicationContext

	/**
	 * Static method which starts loading configuration, resources and a new JDA instance of the bot being created.
	 *
	 * @param clazz main type of class that runs the bot.
	 */
	fun run(clazz: KClass<*>) {
		try {
			val printer = ConsolePrinter()
			val printers = arrayOf(
				FancyTitlePrinter(BANNER_CLASSPATH_LOCATION, printer),
				FancyFramePrinter(FRAME_CLASSPATH_LOCATION, printer),
			)
			printers.forEach { it.print() }
			try {
				log.info("Init Spring Context with base class: {}. Init packages tree: {}.", clazz.qualifiedName, BASE_PACKAGE)
				context = AnnotationConfigApplicationContext(clazz.java)
			} catch (ex: FatalBeanException) {
				throw IrreparableException(ex)
			}
		} catch (ex: IrreparableException) {
			ex.printLogStatement()
			ex.killProcess()
		}
	}
}
