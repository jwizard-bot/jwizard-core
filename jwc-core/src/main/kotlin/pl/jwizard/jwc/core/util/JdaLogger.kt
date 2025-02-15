package pl.jwizard.jwc.core.util

import org.slf4j.Logger
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.GuildCommandBaseContext
import pl.jwizard.jwc.core.util.ext.qualifier

fun Logger.jdaInfo(context: CommandBaseContext, message: String, vararg args: Any?) {
	info(loggerMessageContent(context, message, *args))
}

fun Logger.jdaDebug(context: CommandBaseContext, message: String, vararg args: Any?) {
	debug(loggerMessageContent(context, message, *args))
}

fun Logger.jdaError(context: CommandBaseContext, message: String, vararg args: Any?) {
	error(loggerMessageContent(context, message, *args))
}

private fun loggerMessageContent(
	context: CommandBaseContext,
	message: String, vararg args: Any?,
): String {
	var template = ""
	val messageArgs = mutableListOf<String>()
	if (context is GuildCommandBaseContext) {
		messageArgs += context.author.qualifier
		messageArgs += context.guild.qualifier
		template += "A: %s, G: %s"
	}
	messageArgs += message.format(*args)
	return "$template -> %s".format(*(messageArgs.toTypedArray()))
}
