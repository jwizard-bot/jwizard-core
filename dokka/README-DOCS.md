![](images/banner.png)

[repo](https://github.com/jwizard-bot/jwizard-core)

<br>

JWizard is a multipurpose discord music bot handling audio content from various multimedia streaming sources and custom
playlist management system.

This documentation contains the core of the application, which supports the Discord API and bridges front-end and
back-end layers.

### Other projects

Front-end: [repo](https://github.com/jwizard-bot/jwizard-web)

Back-end (API): [repo](https://github.com/jwizard-bot/jwizard-api)

### Key features

* This project was developed using the Spring IoC architecture (without using Spring Boot), employing loose coupling
  through the SPI architecture and bean interfaces.
* Bean interfaces ensure loose coupling between the project's modules.
* All code was written in Kotlin.
* The Discord API was handled using the JDA (Java Discord API) library.
* OPUS audio support and streaming are provided by the Lavalink client and a modified Lavalink server cluster.
* Communication and event handling between the application and the back-end layer is done using Websockets and RabbitMQ.
