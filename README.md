![](.github/banner.png)

JWizard is an open-source Discord music bot handling audio content from various multimedia sources with innovative web
player. This repository contains the core of the application, which supports the Discord API event handlers, Lavalink
client for sending commands to Lavalink nodes and message broker handling events from web interface.

## Table of content

* [Key features](#key-features)
* [Clone and install](#clone-and-install)
* [Project modules](#project-modules)
* [Architecture concepts](#architecture-concepts)
* [Documentation](#documentation)
* [Contributing](#contributing)
* [License](#license)

## Key features

- [x] **Handling legacy and slash commands**: Concurrent support for prefix commands (starting with $ by default) and
  modern slash commands / supporting the same functions.
- [x] **Audio player**: Ability to play, pause, skip and add audio items to a queue. Support for handling audio content
  from a wide variety of sources, including those marked as NSFW.
- [x] **Internet radio**: Ability to receive broadcasts and view currently playing content of selected radio stations.
  For more information, visit the 'radio' page.
- [x] **Voting system**: Highly customizable voting system that allows skipping the currently playing audio source,
  shuffling the queue and other audio player actions.
- [ ] **Remote playback via web browser**: An innovative system for playing audio content without the need for commands,
  using a browser interface and the capabilities offered by real-time communication.
- [ ] **Audio playlists**: Save, manage and playback audio content stored in a proprietary playlist system. Interaction
  through commands and a web browser.
- [ ] **Guild management**: Manage commands, audio player settings, guild settings, and playlists assigned to a user
  account through a web browser.

## Clone and install

1. Make sure you have at least JVM 17 and Kotlin 2.0.
2. Go to [JWizard Infra repository](https://github.com/jwizard-bot/jwizard-infra), configure and run all necessary
   containers defined in `README.md` file in this repository. You must have up these containers:

| Name                | Port(s)    | Description                  |
|---------------------|------------|------------------------------|
| jwizard-minio-s3    | 8763, 8764 | Storage objects service.     |
| jwizard-vault       | 8761       | Secret keys storage service. |
| jwizard-mysql-db    | 8762       | MySQL database.              |
| jwizard-lava-node-1 | 8766       | Lavalink #1 node.            |
| jwizard-lava-node-2 | 8767       | Lavalink #2 node.            |

> NOTE: Alternatively, you can run single Lavalink node, but in `application-dev.yml` you must remove second Lavalink
> node declaration. Running 2 nodes are useful for checking load-balancer in performance tests.

3. Clone this repository via:

```shell
$ git clone https://github.com/jwizard-bot/jwizard-core
```

4. Create `.env` file in root of the project path (based on `example.env`) and insert Vault token:

```properties
ENV_VAULT_TOKEN=<vault token>
```

where `<value token>` property is the Vault token generated during the initial launch of the Hashicorp Vault server.

5. That's it. Now you can run via Intellij IDEA. Make sure, you have set JVM parameters:

```
-Druntime.profiles=dev -Denv.enabled=true -Xms128m -Xmx1G
```

where `Xmx` and `Xms` parameters are optional and can be modified.

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

## Architecture concepts

* This project was developed using the Spring IoC architecture (without using Spring Boot), employing loose coupling
  through the SPI architecture and bean interfaces.
* Bean interfaces ensure loose coupling between the project's modules.
* All code was written in Kotlin.
* The Discord API was handled using the JDA (Java Discord API) library.
* OPUS audio support and streaming are provided by the Lavalink client and a modified Lavalink server cluster.
* Communication and event handling between the application and the back-end layer is done using Websockets and RabbitMQ.

## Documentation

For detailed documentation, please visit [JWizard Core KDoc documentation](https://docs.jwizard.pl/jwc).

## Contributing

We welcome contributions from the community! Please read our [CONTRIBUTE](./CONTRIBUTE.md) file for guidelines on how
to get involved.

## License

This project is licensed under the AGPL-3.0 License - see the LICENSE file for details.
