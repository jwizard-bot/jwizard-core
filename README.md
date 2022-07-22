# Franek Bot
[![Generic badge](https://img.shields.io/badge/Made%20in-Java%20SE%2011-1abc9c.svg)](https://www.java.com/en/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Build%20with-Maven-green.svg)](https://maven.apache.org/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Packaging-Fat%20jar-brown.svg)](https://maven.apache.org/)&nbsp;&nbsp;
<br><br>
A multi-functional music bot (likely to be enhanced with additional features over time) that allows you to play, pause, repeat and queue songs on the popular VoIP Discord platform. Written entirely using Java SE 11. The bot also includes a proprietary log system I wrote (with the ability to log to a file and standard output). Configuration of the system is done in the `logger-config.json` configuration file.

## Dependencies
- [Discord JDA](https://github.com/DV8FromTheWorld/JDA)
- [Discord JDA Utilities](https://github.com/JDA-Applications/JDA-Utilities)
- [Lavaplayer](https://github.com/sedmelluq/lavaplayer)
- [JacksonJSON](https://github.com/FasterXML/jackson)
- [Project Lombok](https://projectlombok.org/)
- [Maven Assemby Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/)

## Clone, prepare and run
To install the program on your computer use the command (or use the built-in GIT system in your IDE environment):
```
$ git clone https://github.com/Milosz08/JDA_Discord_Bot
```
### Prepare and run
The application was split into a production start-up and a development start-up. There are different configuration files for the two versions. For the development version it is dev-config.json and for the production version it is prod-config.json, respectively.
With this configuration, it is possible to run the production version on the server at the same time (continuity of service), and the development version on the computer, where new functionalities can be added and tested.
> NOTE: The production and development versions are actually separate instances of discord bots. Two separate applications must be created for them to work properly.
* Change file name from `example-prod-config.json`/`example-dev-config.json` to `prod-config.json`/`dev-config.json` and fill with the appropriate values:
```js
{
    "showFancyTitle": true, // showing ASCII letters title, when application starting
    "botVersion": "1.0", // version of bot
    "developmentMode": true, // in prod-config.json file must be false, in dev-config.json must be true
    "token": "xxxxxx", // discord bot token (for find more, goto https://discord.com/developers/)
    "applicationId": "xxxxxx", // discord application identifier (for find more, goto https://discord.com/developers/)
    "defPrefix": "$", // default bot prefix, which used to invoke all commands
    "queuePaginationMaxElmsOnPage": 20, // max elements in embed message on single page
    "maxInactivityTimeMinutes": 5, // max inactivity time, after bot leaving voice channel (if less than 0, not leave)
    "maxVotingElapseTimeMinutes": 2 // max time in which to conduct the vote (if less than 0, no maximum time)
}
```
* Set the appropriate logger parameters in `logger-config.json`
```js
{
    "loggerEnabled": true, // enable/disable logger
    "loggerSensitivity": [ // saving information based on array parameters
        "INFO", "WARN", "ERROR"
    ],
    "enableLoggedToStandardOutput": true, // enable/disable logging values in console
    "enableLoggedToFileOutput": true // enable/disable save logs into .log files
}
```
> NOTE: By default, the application saves logs in the `/target` directory, while in the production (.jar) version this will be the folder in which the application will be launched.
* To run the application in the development mode, use the --dev switch. Running the application without any arguments will load the production version configuration file.
```
$ java --dev FranekBot
```

## Running in the background (daemon)
In order to run the programme as a separate server process (on a machine running UNIX, for example Linux), it is recommended to use a daemon. You can read how to use the programme on the [official website](https://manpages.ubuntu.com/manpages/kinetic/en/man1/daemon.1.html). Alternatively, you can run the programme as a separate process and save the PID to a file. To do this, use the command:
```
$ nohup java -jar -Xmx512m franek-bot_1.0-SNAPSHOT-jar-with-dependencies.jar <dev/null 2>$1 | tee logfile.log &
```

## JAR package
The application has been prepared to make package of this application into a JAR executable file. All you need to do is run the `clean` option in the IDE and then `package` in the `Maven` tab. To run the packaged jar file, use the following command:
```
$ java -jar -Xmx512m franek-bot_1.0-SNAPSHOT-jar-with-dependencies.jar
```
## License
This application is on MIT License.
