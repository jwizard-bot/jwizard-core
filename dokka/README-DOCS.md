# JWizard API

![](images/banner.png)

JWizard is an open-source Discord music bot handling audio content from various multimedia sources with innovative web
player. This documentation contains the core of the application, which supports the Discord API event handlers, Lavalink
client for sending commands to Lavalink nodes and message broker handling events from web interface.

### Project modules

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

### Architecture concepts

* This project was developed using the Spring IoC architecture (without using Spring Boot), employing loose coupling
  through the SPI architecture and bean interfaces.
* Bean interfaces ensure loose coupling between the project's modules.
* All code was written in Kotlin.
* The Discord API was handled using the JDA (Java Discord API) library.
* OPUS audio support and streaming are provided by the Lavalink client and a modified Lavalink server cluster.
* Communication and event handling between the application and the back-end layer is done using Websockets and RabbitMQ.
