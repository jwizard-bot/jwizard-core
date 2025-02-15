package pl.jwizard.jwc.audio.gateway.ws

// websocket close codes
internal enum class WsCode(val code: Int) {
	NORMAL(1000),
	;

	fun isEqual(deliveredCode: Int) = code == deliveredCode
}
