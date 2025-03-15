![](.github/banner.png)

[[About project](https://jwizard.pl/about)]
| [[Docker image](https://hub.docker.com/r/milosz08/jwizard-core)]
| [[Docker installation](./docker/README.md)]

JWizard is an open-source Discord music bot that manages audio content from various multimedia
sources and features an innovative web player. This repository contains the core of the application,
which supports Discord API event handlers and a message broker for handling events from the web
interface. It is also designed for clustering, utilizing a shard-offset system.

## Table of content

* [Architecture concepts](#architecture-concepts)
* [Project modules](#project-modules)
* [Audio gateway client](#audio-gateway-client)
* [Clone and install](#clone-and-install)
* [Contributing](#contributing)
* [License](#license)

## Architecture concepts

* This project was built using the Spring IoC architecture (without Spring Boot), ensuring loose
  coupling through the SPI architecture and bean interfaces.
* Bean interfaces help maintain loose coupling between the project's modules.
* The entire codebase is written in Kotlin.
* The Discord API is integrated using the JDA (Java Discord API) library.
* OPUS audio support and streaming are handled by the custom audio gateway client and a modified
  Lavalink server cluster.
* Communication and event handling between the application and the back-end are managed via
  WebSockets and RabbitMQ.

## Project modules

| Name              | Description                                                                                                                     |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------|
| jwc-api           | JDA command handlers for capturing interactions triggered by Discord guild members.                                             |
| jwc-app           | The application entry point, including configuration files and internationalization (i18n) content.                             |
| jwc-audio         | JWizard audio client bridge, node manager, audio content loaders, and schedulers.                                               |
| jwc-audio-gateway | Audio gateway facilitating communication between audio servers and the jwc-audio module.                                        |
| jwc-command       | Framework for legacy (prefix) and slash command interactions, including component handlers and command reflection loader.       |
| jwc-core          | JDA loader, configuration loader framework, SPI interfaces for jwc-audio, JVM thread helpers, and utility formatters.           |
| jwc-exception     | Set of exceptions that may be thrown during the interaction pipeline, caught by the command interaction framework.              |
| jwc-gateway       | Handle communication between Core and other JWizard projects (exclude communication with audio server) via HTTP, REST and AMQP. |
| jwc-persistence   | Provides communication via S3 storage and RDBMS (SQL), with loosely coupled binding beans (provided through SPI).               |
| jwc-radio         | Framework for parsing radio playback data and associated information.                                                           |
| jwc-vote          | JDA voting framework that manages voting interactions via interaction components.                                               |

## Audio gateway client

This project contains custom client for Lavalink nodes (`jwc-audio-gateway` module). It enables the
creation of separate node pools and implements load balancing based on the Discord gateway audio
region. This client is fully compatible with the Lavalink v4 protocol.

Key concepts:

* A modified version of the original Lavalink client for Java/Kotlin, supporting Lavalink v4.
* Enables node pool fragmentation, allowing nodes to be categorized, restricted, or prioritized
  for handling playback requests - useful for distributing traffic across nodes running different
  audio plugins.
* Each node is represented by an independent Lavalink server instance.
* Load balancing within a selected node pool ensures that playback requests are handled by a node
  located in the same region as the Discord voice server.
* Additionally, the system selects the least loaded node from the pool based on a penalty system.
* Each link (guild representation) dynamically assigns a node, automatically switching to another
  node in case of failure, based on the load balancing algorithm.
* Connections to Lavalink servers are established via HTTP (REST) and WebSocket protocols.

## Clone and install

1. Make sure you have at least JDK 17 and Kotlin 2.0.
2. Clone **JWizard Lib** and **JWizard Tools** from organization repository via:

```bash
$ git clone https://github.com/jwizard-bot/jwizard-lib
$ git clone https://github.com/jwizard-bot/jwizard-tools
```

3. Configure and run all necessary containers defined in `README.md` file
   in [jwizard-lib](https://github.com/jwizard-bot/jwizard-lib) repository. You must have up these
   containers:

| Name                | Port(s) | Description                  |
|---------------------|---------|------------------------------|
| jwizard-vault       | 8761    | Secret keys storage service. |
| jwizard-mysql-db    | 8762    | MySQL database.              |
| jwizard-lava-node-1 | 8766    | Lavalink #1 node.            |
| jwizard-lava-node-2 | 8767    | Lavalink #2 node.            |

> NOTE: Don't forget to perform database migration after start DB (see
> [jwizard-lib](https://github.com/jwizard-bot/jwizard-lib) repository).

> NOTE: Alternatively, you can run single Lavalink node, but in `docker-compose.yml` file in
> [jwizard-lib](https://github.com/jwizard-bot/jwizard-lib) repository you must remove second
> Lavalink node declaration.
> Running 2 nodes are useful for checking load-balancer in performance tests.

4. Build library and package to Maven Local artifacts' storage (for **JWizard Lib**):

* for UNIX based systems:

```bash
$ ./gradlew clean publishToMavenLocal
```

* for Windows systems:

```bash
.\gradlew clean publishToMavenLocal
```

5. Clone this repository via:

```bash
$ git clone https://github.com/jwizard-bot/jwizard-core
```

6. Create `.env` file in root of the project path (based on `example.env`) and insert Vault token:

```properties
ENV_VAULT_TOKEN=<vault token>
```

where `<value token>` property is the Vault token stored in configured `.env` file
in [jwizard-lib](https://github.com/jwizard-bot/jwizard-lib) repository.

7. That's it. Now you can run via Intellij IDEA. Make sure, you have set JVM parameters:

```bash
-Druntime.profiles=dev
-Denv.enabled=true # optional, if false JWizard will prevent read .env file
-Dserver.port=<port number> # only for production, for dev port is already pre-defined in vault

-Djda.instance.name=core-instance/N # bot instance refer to Vault backend prefix
-Djda.sharding.offset.start=<number> # shard ID which starts (inclusive) shards pool in process
-Djda.sharding.offset.end=<number> # shard ID which end (inclusive) shards pool in process
-Djda.sharding.total-shards=<number> # total shards for all processes in one instance

-Xms1G -Xmx1G # optional, see NOTE
# ... rest parameters, ex. JVM GC configuration
```

where:

* `N` is instance number (`0` or `1`),
* `Xmx` and `Xms` parameters are optional and can be modified.

> NOTE: For servers running on HotSpot JVM, Oracle recommended same Xms and Xmx parameter, ex.
`-Xms1G` and `-Xmx1G`. More information you will find
> [here](https://docs.oracle.com/cd/E74363_01/ohi_vbp_-_installation_guide--20160224-094432-html-chunked/s66.html).

> NOTE: You can run concurrently 2 instances, but you must set valid offsets in
`-Djda.sharding.offset.start` and `-Djda.sharding.offset.end`. Concurrent instances can share same
> Lavalink node/nodes.

### Clustering example with multiple concurrent instance

```
instance 0              instance 1       ...     instance N
├─ process 0            ├─ process 0
│  ├─ shards 0-9        │  ├─ shards 0-9
├─ process 1            ├─ process 1
│  ├─ shards 10-19      │  ├─ shards 10-19
│  ...                  │  ...
├─ process N            ├─ process N
```

More about sharding, clustering multiple concurrent instances and shards fragmentation (different
shard ranges for distributed JVM architecture) you will find here:

* [https://discord.com/developers/docs/events/gateway#sharding](https://discord.com/developers/docs/events/gateway#sharding)
* [https://skelmis.co.nz/posts/discord-bot-sharding-and-clustering](https://skelmis.co.nz/posts/discord-bot-sharding-and-clustering)

## Contributing

We welcome contributions from the community! Please read our [CONTRIBUTING](./CONTRIBUTING.md) file
for guidelines on how to get involved.

## License

This project is licensed under the AGPL-3.0 License - see the LICENSE file for details.
