package pl.jwizard.jwc.command.interaction

interface MenuOption {
	// unique key representing the menu option
	val key: String

	// value associated with the menu option
	val value: String

	// formatted string representation of the option for embedding in messages
	val formattedToEmbed: String
}

