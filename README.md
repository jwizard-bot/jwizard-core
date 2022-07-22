# Franek Bot
A multi-functional music bot (likely to be enhanced with additional features over time) that allows you to play, pause, repeat and queue songs on the popular VoIP Discord platform. Written entirely using Java SE 11. The bot also includes a proprietary log system I wrote (with the ability to log to a file and standard output). Configuration of the system is done in the `logger-config.json` configuration file.

## Dependencies
- [Discord JDA](https://github.com/DV8FromTheWorld/JDA)
- [Discord JDA Utilities](https://github.com/JDA-Applications/JDA-Utilities)
- [Lavaplayer](https://github.com/sedmelluq/lavaplayer)
- [JacksonJSON](https://github.com/FasterXML/jackson)
- [Project Lombok](https://projectlombok.org/)
- [Maven Assemby Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/)

## Clone, prepare and run
* To install the program on your computer use the command (or use the built-in GIT system in your IDE environment):
```
$ git clone https://github.com/Milosz08/JDA_Discord_Bot
```
* Change file name from `config-example.json` to `config.json` and fill with the appropriate values:
```js
{
    "showFancyTitle": true, // showing ASCII letters title, when application starting
    "botVersion": "1.0", // version of bot
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

## Running in the background (daemon)
In order to run the programme as a separate server process (on a machine running UNIX, for example Linux), it is recommended to use a daemon. You can read how to use the programme on the [official website](https://manpages.ubuntu.com/manpages/kinetic/en/man1/daemon.1.html). Alternatively, you can run the programme as a separate process and save the PID to a file. To do this, use the command:
```
$ nohup java -jar franek-bot_1.0-SNAPSHOT-jar-with-dependencies.jar <dev/null 2>$1 | tee logfile.log &
```

## JAR package
The application has been prepared to make package of this application into a JAR executable file. All you need to do is run the `clean` option in the IDE and then `package` in the `Maven` tab. To run the packaged jar file, use the following command:
```
$ java -jar franek-bot_1.0-SNAPSHOT-jar-with-dependencies.jar
```
## License
This application is on MIT License.
