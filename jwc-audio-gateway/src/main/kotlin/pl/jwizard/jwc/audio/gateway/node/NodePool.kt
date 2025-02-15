package pl.jwizard.jwc.audio.gateway.node

// nodes might be grouped in separated pools cannot infer to each others
// for example, one pool for audio servers supports soundcloud and another pool for audio servers
// supports twitch or kick
interface NodePool {
	val poolName: String
}
