![](.github/banner.png)

JWizard is an open-source Discord music bot handling audio content from various multimedia sources with innovative web
player. This repository contains the core of the application, which supports the Discord API event handlers, Lavalink
client for sending commands to Lavalink nodes and message broker handling events from web interface.

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
* OPUS audio support and streaming are provided by the Lavalink client and a modified Lavalink server cluster.
* Communication and event handling between the application and the back-end layer is done using Websockets and RabbitMQ.

## Project modules

| Name            | Description                                                                                                                    |
|-----------------|--------------------------------------------------------------------------------------------------------------------------------|
| jwc-api         | JDA command handlers using for grabbing interaction invoking by Discord guild member.                                          |
| jwc-app         | Application entrypoint, configuration files and i18n local content.                                                            |
| jwc-audio       | Lavalink client, audio nodes manager, audio content loaders and schedulers.                                                    |
| jwc-command     | Legacy (prefix) and slash command interactions framework, interaction component handlers and command reflect loader framework. |
| jwc-core        | JDA loader, configuration loader framework, SPI interfaces for jwc-audio, JVM thread helpers, util formatters.                 |
| jwc-exception   | Set of exceptions which may be thrown in interaction pipeline and grab by command interactions framework.                      |
| jwc-persistence | Provide communication via S3 storage and RDBMS (SQL) with loosely coupled binding beans (provided by SPI).                     |
| jwc-radio       | Radio playback data information's parsing framework.                                                                           |
| jwc-vote        | JDA voting framework which handles voting via interaction components.                                                          |

## Clone and install

1. Make sure you have at least JDK 17 and Kotlin 2.0.
2. Go to [JWizard Infra repository](https://github.com/jwizard-bot/jwizard-infra), configure and run all necessary
   containers defined in `README.md` file in this repository. You must have up these containers:

| Name                | Port(s)    | Description                  |
|---------------------|------------|------------------------------|
| jwizard-minio-s3    | 8763, 8764 | Storage objects service.     |
| jwizard-vault       | 8761       | Secret keys storage service. |
| jwizard-mysql-db    | 8762       | MySQL database.              |
| jwizard-lava-node-1 | 8766       | Lavalink #1 node.            |
| jwizard-lava-node-2 | 8767       | Lavalink #2 node.            |

> NOTE: Alternatively, you can run single Lavalink node, but in `application.dev.yml` you must remove second Lavalink
> node declaration. Running 2 nodes are useful for checking load-balancer in performance tests.

3. Clone **JWizard Lib** from organization repository via:

```bash
$ git clone https://github.com/jwizard-bot/jwizard-lib
```

4. Build library and package to Maven Local artifacts' storage:

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

where `<value token>` property is the Vault token stored in configured `.env` file in `jwizard-infa` repository.

5. That's it. Now you can run via Intellij IDEA. Make sure, you have set JVM parameters:

```
-Druntime.profiles=dev -Denv.enabled=true -Xms1G -Xmx1G
```

where `Xmx` and `Xms` parameters are optional and can be modified.

> NOTE: For servers running on HotSpot JVM, Oracle recommended same Xms and Xmx parameter, ex. `-Xms1G` and `-Xmx1G`.
> More information you will
> find [here](https://docs.oracle.com/cd/E74363_01/ohi_vbp_-_installation_guide--20160224-094432-html-chunked/s66.html).

## Documentation

For detailed documentation, please visit [JWizard documentation](https://jwizard.pl/docs).
<br>
Documentation for latest version (with SHA) you will find [here](https://docs.jwizard.pl/jwc) - in KDoc format.

## Contributing

We welcome contributions from the community! Please read our [CONTRIBUTE](./CONTRIBUTE.md) file for guidelines on how
to get involved.

## License

This project is licensed under the AGPL-3.0 License - see the LICENSE file for details.
