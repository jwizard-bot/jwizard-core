/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.manager

import dev.arbjerg.lavalink.VERSION
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.apache.commons.io.FileUtils
import pl.jwizard.jwc.api.ManagerCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.interaction.component.RefreshableComponent
import pl.jwizard.jwc.command.interaction.component.RefreshableContent
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.refer.CommandArgument
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nSystemSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.PercentageIndicatorBar
import pl.jwizard.jwc.core.jvm.SystemProperty
import pl.jwizard.jwc.core.util.ext.versionFormat
import pl.jwizard.jwc.core.util.mdBold
import pl.jwizard.jwl.property.AppBaseProperty
import java.util.*

/**
 * Command class responsible for displaying debug information about the bot, system, and Lavalink client status. This
 * command provides details such as memory usage, build version, and Lavalink nodes information.
 *
 * @param commandEnvironment The environment context for command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.DEBUG)
class DebugCmd(
	commandEnvironment: CommandEnvironmentBean
) : ManagerCommandBase(commandEnvironment), RefreshableContent<CommandContext> {

	/**
	 * Executes the debug command, generating a response with detailed information about the bot, system, and Lavalink
	 * client status. It also adds a refresh button for updating the information.
	 *
	 * @param context The context in which the command is executed.
	 * @param response The response object that will send the embed message.
	 */
	override fun executeManager(context: CommandContext, response: TFutureResponse) {
		val refreshableComponent = RefreshableComponent(i18nBean, eventQueueBean, this, context)
		refreshableComponent.initEvent()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(createDebugMessage(context))
			.addActionRows(refreshableComponent.createRefreshButtonRow(context))
			.build()
		response.complete(commandResponse)
	}

	/**
	 * Determines whether the command response should be sent privately to the user. If the "private" argument is passed,
	 * the message will be sent privately to the user.
	 *
	 * @param context The context in which the command is executed.
	 * @return The user ID if the message should be sent privately, or null otherwise.
	 */
	override fun isPrivate(context: CommandContext): Long? {
		val isPrivate = context.getNullableArg<Boolean>(CommandArgument.PRIVATE)
		return if (isPrivate == true) context.author.idLong else null
	}

	/**
	 * Refreshes the debug information when the user interacts with the refresh button. Updates the message with the
	 * latest system and bot information.
	 *
	 * @param event The button interaction event triggered by the user.
	 * @param response The mutable list of embed messages that will be updated.
	 * @param payload The original command context.
	 */
	override fun onRefresh(event: ButtonInteractionEvent, response: MutableList<MessageEmbed>, payload: CommandContext) {
		response.add(createDebugMessage(payload))
	}

	/**
	 * Generates the debug message containing information about the bot's version, system memory usage, and Lavalink
	 * client status. It also provides a list of available Lavalink nodes with memory and CPU usage details.
	 *
	 * @param context The context in which the command is executed.
	 * @return The generated embed message with debug information.
	 */
	private fun createDebugMessage(context: CommandContext): MessageEmbed {
		val buildVersion = environmentBean.getProperty<String>(AppBaseProperty.DEPLOYMENT_BUILD_VERSION)
		val latestTag = environmentBean.getProperty<String>(AppBaseProperty.RELEASE_LATEST_TAG)
		val buildDate = environmentBean.getProperty<String>(AppBaseProperty.DEPLOYMENT_LAST_BUILD_DATE)

		val runtime = Runtime.getRuntime()
		val totalMemory = runtime.maxMemory()
		val usedMemory = runtime.totalMemory() - runtime.freeMemory()
		val percentageIndicatorBar = PercentageIndicatorBar(usedMemory, totalMemory)

		val messageBuilder = createEmbedMessage(context)
			.setTitle(I18nSystemSource.DEBUG_INFO_HEADER)
			.setKeyValueField(I18nSystemSource.COMPILATION_VERSION, "$latestTag ($buildVersion)")
			.setSpace()
			.setKeyValueField(I18nSystemSource.DEPLOYMENT_DATE, buildDate)

		for ((index, entry) in SystemProperty.entries.withIndex()) {
			messageBuilder.setKeyValueField(entry.i18nSystemSource, environmentBean.getProperty(entry.botProperty))
			if (index % 2 == 0) {
				messageBuilder.setSpace()
			}
		}
		val lavaNodesInfo = commandEnvironment.distributedAudioClientSupplier.availableNodes.joinToString("\n") {
			val memory = it.stats?.memory
			val usedMem = FileUtils.byteCountToDisplaySize(memory?.used)
			val maxMem = FileUtils.byteCountToDisplaySize(memory?.reservable)
			val memoryBar = PercentageIndicatorBar(memory?.used ?: 0, memory?.reservable ?: 0)
			val cpuStats = "%.2f%%".format(it.stats?.cpu?.lavalinkLoad)

			val info = it.getNodeInfo().toFuture().get()
			val joiner = StringJoiner("")

			joiner.add("* ${mdBold(it.name)} (CPU: $cpuStats) (MEM: $usedMem/$maxMem)\n")
			joiner.add("  ${memoryBar.generateBar(showPercentageNumber = true)}\n")
			joiner.add("  Lavalink: ${info.version.semver.versionFormat}, Lavaplayer: ${info.lavaplayer.versionFormat}")
			joiner.toString()
		}
		return messageBuilder
			.setKeyValueField(I18nSystemSource.JVM_USED_MEMORY, FileUtils.byteCountToDisplaySize(usedMemory))
			.setSpace()
			.setKeyValueField(I18nSystemSource.JVM_XMX_MEMORY, FileUtils.byteCountToDisplaySize(totalMemory))
			.setKeyValueField(
				I18nSystemSource.JVM_MEMORY_USAGE,
				percentageIndicatorBar.generateBar(showPercentageNumber = true),
				inline = false
			)
			.setKeyValueField(I18nSystemSource.JDA_VERSION, JDAInfo.VERSION.versionFormat)
			.setSpace()
			.setKeyValueField(I18nSystemSource.LAVALINK_CLIENT_VERSION, VERSION.versionFormat)
			.setKeyValueField(I18nSystemSource.AVAILABLE_LAVALINK_NODES, lavaNodesInfo, inline = false)
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
