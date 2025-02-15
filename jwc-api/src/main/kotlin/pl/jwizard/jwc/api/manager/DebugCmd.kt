package pl.jwizard.jwc.api.manager

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.ManagerCommandBase
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
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

@JdaCommand(Command.DEBUG)
class DebugCmd(
	private val deploymentSupplier: VcsDeploymentSupplier,
	private val vcsConfigBean: VcsConfigBean,
	commandEnvironment: CommandEnvironmentBean,
) : ManagerCommandBase(commandEnvironment) {
	override fun executeManager(context: GuildCommandContext, response: TFutureResponse) {
		val refreshableComponent = createRefreshable {
			it.add(createDebugMessage(context))
		}
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

	override fun isPrivate(context: GuildCommandContext): Long? {
		val isPrivate = context.getNullableArg<Boolean>(Argument.PRIVATE)
		return if (isPrivate == true) context.author.idLong else null
	}

	private fun createDebugMessage(context: CommandBaseContext): MessageEmbed {
		val repository = VcsRepository.JWIZARD_CORE
		val details = deploymentSupplier
			.getDeploymentDetails(vcsConfigBean.getRepositoryName(repository))

		val (name, url) = vcsConfigBean.createSnapshotUrl(repository, details?.longSHA)

		val runtime = Runtime.getRuntime()
		val totalMemory = runtime.maxMemory()
		val usedMemory = runtime.totalMemory() - runtime.freeMemory()
		val percentageIndicatorBar = PercentageIndicatorBar(usedMemory, totalMemory)

		val messageBuilder = createEmbedMessage(context)
			.setTitle(I18nSystemSource.DEBUG_INFO_HEADER)
			.setKeyValueField(
				I18nSystemSource.COMPILATION_VERSION,
				if (name != null) mdLink(name, url) else null
			)
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
}
