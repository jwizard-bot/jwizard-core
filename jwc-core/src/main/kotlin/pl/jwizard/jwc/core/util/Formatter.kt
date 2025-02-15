package pl.jwizard.jwc.core.util

fun formatQualifier(name: String, id: Long) = "\"%s <@%s>\"".format(name, id)

fun mdLink(name: Any?, link: String?) = "[$name]($link)"

fun mdBold(text: String) = "**$text**"

fun mdCode(text: String?) = "`$text`"

fun mdList(text: String?, eol: Boolean = false) = "* $text${if (eol) "\n" else ""}"

fun keyValueFormat(key: String, value: Any?) = "$key: $value"
