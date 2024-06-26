/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.exception

import pl.jwizard.core.command.CompoundCommandEvent

object CommandException {
	class UsedCommandOnForbiddenChannelException(event: CompoundCommandEvent) : AbstractBotException(
		event, UsedCommandOnForbiddenChannelException::class,
		i18nLocale = I18nExceptionLocale.USED_COMMAND_ON_FORBIDDEN_CHANNEL,
		logMessage = "Attempt to invoke command on forbidden channel"
	)

	class VolumeUnitsOutOfBoundsException(
		event: CompoundCommandEvent,
		minVolume: Int,
		maxVolume: Int,
	) : AbstractBotException(
		event, VolumeUnitsOutOfBoundsException::class,
		i18nLocale = I18nExceptionLocale.VOLUME_UNITS_OUT_OF_BOUNDS,
		variables = mapOf(
			"minVolume" to minVolume,
			"maxVolume" to maxVolume,
		),
		logMessage = "Attempt to set out of bounds audio player volume units"
	)

	class CommandIsTurnedOffException(event: CompoundCommandEvent, commandName: String) : AbstractBotException(
		event, CommandIsTurnedOffException::class,
		i18nLocale = I18nExceptionLocale.COMMAND_IS_TURNED_OFF,
		variables = mapOf("command" to commandName),
		logMessage = "Attempt to execute turned off command. Command: $commandName"
	)

	class MismatchCommandArgumentsException(
		event: CompoundCommandEvent,
		command: String,
		syntax: String,
	) : AbstractBotException(
		event, MismatchCommandArgumentsException::class,
		i18nLocale = I18nExceptionLocale.MISMATCH_COMMAND_ARGS,
		variables = mapOf("syntax" to syntax),
		logMessage = "Attempt to invoke command $command with non-exact arguments",
	)

	class ViolatedCommandArgumentOptionsException(
		event: CompoundCommandEvent,
		violatedArgName: String,
		violatedValue: Any,
		acceptedValueList: List<String>,
		acceptedValuesFormatted: String,
	) : AbstractBotException(
		event, ViolatedCommandArgumentOptionsException::class,
		i18nLocale = I18nExceptionLocale.VIOLATED_COMMAND_ARG_OPTIONS,
		variables = mapOf(
			"violatedArgName" to violatedArgName,
			"acceptedValueList" to acceptedValuesFormatted,
		),
		logMessage = "Attempt to invoke command with violated argument $violatedArgName options. " +
			"Violated value: $violatedValue. Accepted values: $acceptedValueList",
	)

	class CommandAvailableOnlyForDiscreteTrackException(event: CompoundCommandEvent) : AbstractBotException(
		event, CommandAvailableOnlyForDiscreteTrackException::class,
		i18nLocale = I18nExceptionLocale.COMMAND_AVAILABLE_ONLY_FOR_DISCRETE_TRACK,
		logMessage = "Attempt to invoke command on current continuous audio source, while is avaialbe only discrete source"
	)
}
