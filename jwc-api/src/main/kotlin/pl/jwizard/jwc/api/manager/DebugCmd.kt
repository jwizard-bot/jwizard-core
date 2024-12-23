/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.manager

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.ManagerCommandBase
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.interaction.component.RefreshableContent
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.config.DeploymentDetails
import pl.jwizard.jwc.core.config.spi.VcsDeploymentSupplier
import pl.jwizard.jwc.core.i18n.source.I18nActionSource
import pl.jwizard.jwc.core.i18n.source.I18nSystemSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.PercentageIndicatorBar
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.util.mdBold
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwc.core.util.mdList
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.util.formatBytes
import pl.jwizard.jwl.vcs.VcsConfigBean
import pl.jwizard.jwl.vcs.VcsRepository
import java.util.*

/**
 * Command class responsible for displaying debug information about the bot, system, and audio client status. This
 * command provides details such as memory usage, build version, and audio nodes information.
 *
 * @property deploymentSupplier Provides deployment details from version control, including version and commit info.
 * @property vcsConfigBean Handles version control configuration and builds URLs for specific VCS snapshots.
 * @param commandEnvironment The environment context for command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.DEBUG)
class DebugCmd(
	private val deploymentSupplier: VcsDeploymentSupplier,
	private val vcsConfigBean: VcsConfigBean,
	commandEnvironment: CommandEnvironmentBean,
) : ManagerCommandBase(commandEnvironment), RefreshableContent<CommandBaseContext> {

	/**
	 * Executes the debug command, generating a response with detailed information about the bot, system, and audio client
	 * status. It also adds a refresh button for updating the information.
	 *
	 * @param context The context in which the command is executed.
	 * @param response The response object that will send the embed message.
	 */
	override fun executeManager(context: GuildCommandContext, response: TFutureResponse) {
		val refreshableComponent = createRefreshable(this, context)
		refreshableComponent.initEvent()

		val actionRow = ActionRow.of(
			refreshableComponent.createRefreshButton(context),
			createLinkButton(I18nActionSource.STATUS, BotProperty.LINK_STATUS, context),
		)
		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(createDebugMessage(context))
			.addActionRows(actionRow)
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
	override fun isPrivate(context: GuildCommandContext): Long? {
		val isPrivate = context.getNullableArg<Boolean>(Argument.PRIVATE)
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
	override fun onRefresh(
		event: ButtonInteractionEvent,
		response: MutableList<MessageEmbed>,
		payload: CommandBaseContext,
	) {
		response.add(createDebugMessage(payload))
	}

	/**
	 * Generates the debug message containing information about the bot's version, system memory usage, and audio client
	 * status. It also provides a list of available audio nodes with memory and CPU usage details.
	 *
	 * @param context The context in which the command is executed.
	 * @return The generated embed message with debug information.
	 */
	private fun createDebugMessage(context: CommandBaseContext): MessageEmbed {
		val (coreUrl, details) = getDeploymentAppData(VcsRepository.JWIZARD_CORE)

		val runtime = Runtime.getRuntime()
		val totalMemory = runtime.maxMemory()
		val usedMemory = runtime.totalMemory() - runtime.freeMemory()
		val percentageIndicatorBar = PercentageIndicatorBar(usedMemory, totalMemory)

		val messageBuilder = createEmbedMessage(context)
			.setTitle(I18nSystemSource.DEBUG_INFO_HEADER)
			.setKeyValueField(I18nSystemSource.COMPILATION_VERSION, coreUrl)
			.setSpace()
			.setKeyValueField(I18nSystemSource.DEPLOYMENT_DATE, details?.lastUpdatedUtc.toString())

		val availableNodes = commandEnvironment.audioClient.availableNodes
		val audioNodesInfo = availableNodes.joinToString("\n") {
			val memory = it.stats?.memory
			val usedMem = formatBytes(memory?.used)
			val maxMem = formatBytes(memory?.reservable)
			val memoryBar = PercentageIndicatorBar(memory?.used ?: 0, memory?.reservable ?: 0)
			val cpuStats = "%.2f%%".format(it.stats?.cpu?.lavalinkLoad)

			val joiner = StringJoiner("")
			joiner.add(mdList(mdBold(it.name), eol = true))
			joiner.add("  ${memoryBar.generateBar(showPercentageNumber = true)}\n")
			joiner.add("  CPU: $cpuStats, MEM: $usedMem/$maxMem")
			joiner.toString()
		}
		return messageBuilder
			.setKeyValueField(I18nSystemSource.JVM_USED_MEMORY, formatBytes(usedMemory))
			.setSpace()
			.setKeyValueField(I18nSystemSource.JVM_XMX_MEMORY, formatBytes(totalMemory))
			.setKeyValueField(
				I18nSystemSource.JVM_MEMORY_USAGE,
				percentageIndicatorBar.generateBar(showPercentageNumber = true),
				inline = false
			)
			.setKeyValueField(I18nSystemSource.AVAILABLE_AUDIO_NODES, audioNodesInfo, inline = false)
			.setColor(JdaColor.PRIMARY)
			.build()
	}

	/**
	 * Retrieves deployment application data based on the given VCS repository.
	 *
	 * This method fetches deployment details for the provided [VcsRepository] and generates a Markdown link pointing to
	 * a snapshot URL, if available. The method returns a pair containing the Markdown link (or `null` if not applicable)
	 * and the deployment details.
	 *
	 * @param vcsRepository The VCS (Version Control System) repository from which to retrieve deployment data.
	 * @return A pair consisting of:
	 *         - A Markdown-formatted link to the deployment snapshot (or `null` if no link can be created).
	 *         - The associated `DeploymentDetails`, or `null` if no deployment details are available.
	 */
	private fun getDeploymentAppData(vcsRepository: VcsRepository): Pair<String?, DeploymentDetails?> {
		val deploymentDetails = deploymentSupplier.getDeploymentDetails(vcsConfigBean.getRepositoryName(vcsRepository))
		val (name, url) = vcsConfigBean.createSnapshotUrl(vcsRepository, deploymentDetails?.longSHA)
		return Pair(if (name != null) mdLink(name, url) else null, deploymentDetails)
	}
}
