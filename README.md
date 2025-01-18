![](.github/banner.png)

[[About project](https://jwizard.pl/about)]
| [[Docker image](https://hub.docker.com/r/milosz08/jwizard-core)]
| [[Docker installation](./docker/README.md)]

JWizard is an open-source Discord music bot handling audio content from various multimedia sources with innovative web
player. This repository contains the core of the application, which supports the Discord API event handlers and message
broker handling events from web interface.
Use [JWizard Audio Client](https://github.com/jwizard-bot/jwizard-audio-client) for make interactions with Lavalink
nodes. Ready for clustering based on shard-offset system.

## Table of content

* [Architecture concepts](#architecture-concepts)
* [Project modules](#project-modules)
* [Clone and install](#clone-and-install)
* [Documentation](#documentation)
* [Contributing](#contributing)
* [License](#license)

## Architecture concepts

* This project was developed using the Spring IoC architecture (without using Spring Boot), employing loose coupling
  through the SPI architecture and bean interfaces.
* Bean interfaces ensure loose coupling between the project's modules.
* All code was written in Kotlin.
* The Discord API was handled using the JDA (Java Discord API) library.
* OPUS audio support and streaming are provided by the
  custom [JWizard Audio Client](https://github.com/jwizard-bot/jwizard-audio-client) and a modified Lavalink server
  cluster.
* Communication and event handling between the application and the back-end layer is done using Websockets and RabbitMQ.

## Project modules

| Name            | Description                                                                                                                    |
|-----------------|--------------------------------------------------------------------------------------------------------------------------------|
| jwc-api         | JDA command handlers using for grabbing interaction invoking by Discord guild member.                                          |
| jwc-app         | Application entrypoint, configuration files and i18n local content.                                                            |
| jwc-audio       | JWizard audio client bridge, nodes manager, audio content loaders and schedulers.                                              |
| jwc-command     | Legacy (prefix) and slash command interactions framework, interaction component handlers and command reflect loader framework. |
| jwc-core        | JDA loader, configuration loader framework, SPI interfaces for jwc-audio, JVM thread helpers, util formatters.                 |
| jwc-exception   | Set of exceptions which may be thrown in interaction pipeline and grab by command interactions framework.                      |
| jwc-persistence | Provide communication via S3 storage and RDBMS (SQL) with loosely coupled binding beans (provided by SPI).                     |
| jwc-radio       | Radio playback data information's parsing framework.                                                                           |
| jwc-vote        | JDA voting framework which handles voting via interaction components.                                                          |

## Clone and install

1. Make sure you have at least JDK 17 and Kotlin 2.0.
2. Clone **JWizard Lib**, **JWizard Audio Client** and **JWizard Tools** from organization repository via:

```bash
$ git clone https://github.com/jwizard-bot/jwizard-lib
$ git clone https://github.com/jwizard-bot/jwizard-audio-client
$ git clone https://github.com/jwizard-bot/jwizard-tools
```

3. Configure and run all necessary containers defined in `README.md` file
   in [jwizard-lib](https://github.com/jwizard-bot/jwizard-lib) repository. You must have up these containers:

| Name                | Port(s) | Description                  |
|---------------------|---------|------------------------------|
| jwizard-vault       | 8761    | Secret keys storage service. |
| jwizard-mysql-db    | 8762    | MySQL database.              |
| jwizard-lava-node-1 | 8766    | Lavalink #1 node.            |
| jwizard-lava-node-2 | 8767    | Lavalink #2 node.            |

> NOTE: Don't forget to perform database migration after start DB (see
> [jwizard-lib](https://github.com/jwizard-bot/jwizard-lib) repository).

> NOTE: Alternatively, you can run single Lavalink node, but in `docker-compose.yml` file in
> [jwizard-lib](https://github.com/jwizard-bot/jwizard-lib) repository you must remove second Lavalink node declaration.
> Running 2 nodes are useful for checking load-balancer in performance tests.

4. Build library and package to Maven Local artifacts' storage (for **JWizard Lib** and **JWizard Audio Client**):

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

-Djda.instance.name=core-instance-N # bot instance (WARN, this refer to Vault backend prefix, not for clustering key)
-Djda.sharding.cluster=<cluster name (key)> # name of the cluster (also cluster key)
-Djda.sharding.offset.start=<number> # shard ID which starts (inclusive) shards pool in this cluster
-Djda.sharding.offset.end=<number> # shard ID which end (inclusive) shards pool in this cluster

-Xms1G -Xmx1G # optional, see NOTE
# ... rest parameters, ex. JVM GC configuration
```

where:

* `N` is instance number (`0` or `1`),
* `Xmx` and `Xms` parameters are optional and can be modified.

> NOTE: For servers running on HotSpot JVM, Oracle recommended same Xms and Xmx parameter, ex. `-Xms1G` and `-Xmx1G`.
> More information you will
> find [here](https://docs.oracle.com/cd/E74363_01/ohi_vbp_-_installation_guide--20160224-094432-html-chunked/s66.html).

> NOTE: You can run concurrently 2 instances, but you must set valid offsets in `-Djda.sharding.offset.start` and
> `-Djda.sharding.offset.end`. Concurrent instances can share same Lavalink node/nodes.

### Clustering example with multiple concurrent instance

```
instance 0              instance 1       ...     instance N
├─ cluster 0            ├─ cluster 0
│  ├─ shards 0-9        │  ├─ shards 0-9
├─ cluster 1            ├─ cluster 1
│  ├─ shards 10-19      │  ├─ shards 10-19
│  ...                  │  ...
├─ cluster N            ├─ cluster N
```

More about sharding, clustering multiple concurrent instances and shards fragmentation (different shard ranges for
distributed JVM architecture) you will find here:

* [https://discord.com/developers/docs/events/gateway#sharding](https://discord.com/developers/docs/events/gateway#sharding)
* [https://skelmis.co.nz/posts/discord-bot-sharding-and-clustering](https://skelmis.co.nz/posts/discord-bot-sharding-and-clustering)

## Documentation

For detailed documentation, please visit [JWizard documentation](https://jwizard.pl/docs).
<br>
Documentation for latest version (with SHA) you will find:

* [here](https://docs.jwizard.pl/jwc/kdoc) - in **KDoc** format,
* [here](https://docs.jwizard.pl/jwc/javadoc) - in **Javadoc** format.

## Contributing

We welcome contributions from the community! Please read our [CONTRIBUTING](./CONTRIBUTING) file for guidelines on how
to get involved.

## License

This project is licensed under the AGPL-3.0 License - see the LICENSE file for details.
