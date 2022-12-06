# Franek Bot
[![Generic badge](https://img.shields.io/badge/Made%20in-Java%20SE%2011-1abc9c.svg)](https://www.java.com/en/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Build%20with-Maven-green.svg)](https://maven.apache.org/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Packaging-Fat%20jar-brown.svg)](https://maven.apache.org/)&nbsp;&nbsp;
<br><br>
A multi-functional music bot (likely to be enhanced with additional features over time) that allows you to play, pause, repeat and queue songs on the popular VoIP Discord platform. Written entirely using Java SE 11. The bot also includes a proprietary log system I wrote (with the ability to log to a file and standard output).

## Table of content
* [Clone script](#clone-script)
* [Prepare and run](#prepare-and-run)
* [Running in the background (daemon)](#running-in-the-background-daemon)
* [JAR package](#jar-package)
* [Author](#author)
* [License](#license)

<a name="clone-script"></a>
## Clone script
To install the program on your computer use the command (or use the built-in GIT system in your IDE environment):
```
$ git clone https://github.com/Milosz08/JDA_Discord_Bot
```

<a name="prepare-and-run"></a>
## Prepare and run
The application was split into a production start-up and a development start-up. There are different configuration files for the two versions. For the development version it is `dev-config.json` and for the production version it is `prod-config.json`.
With this configuration, it is possible to run the production version on the server at the same time (continuity of service), and the development version on the computer, where new functionalities can be added and tested.
> NOTE: The production and development versions are actually separate instances of discord bots. Two separate applications must be created for them to work properly.
* FOR LOCAL MACHINE: Create `.env` file:
```properties
PROD_TOKEN=xxxxx <- discord api token (available in discord api dashboard)
PROD_APPLICATION_ID=xxxxx <- discord application identifier (available in discord api dashboard)
PROD_DATABASE_CONNECTION_STRING=xxxxx <- ex. jdbc:[dbClient]://[dbServer]:[dbPort]/[dbName]
PROD_DATABASE_USERNAME=xxxxx <- database agent username
PROD_DATABASE_PASSWORD=xxxxx <- database agent password

DEV_TOKEN=xxxxx
DEV_APPLICATION_ID=xxxxx
DEV_DATABASE_CONNECTION_STRING=xxxxx
DEV_DATABASE_USERNAME=xxxxx
DEV_DATABASE_PASSWORD=xxxxx
```
* FOR HEROKU CLOUD: Add environment variables from a file via the GUI or using the command:
```
$ heroku config:set VARIABLE_NAME=variablevalue
```
> NOTE: For more informations about environment variables, go to [heroku website](https://devcenter.heroku.com/articles/config-vars).
* Fill with the appropriate values (skip environment variables):
```json5
{
    "botVersion": "1.0-SNAPSHOT", // version of bot
    "showFancyTitle": true, // showing ASCII letters title, when application starting
    "developmentMode": true, // in prod-config.json file must be false, in dev-config.json must be true
    "authorization": {
        "token": "${DEV_TOKEN}", // refeer to environment variable DEV_TOKEN
        "applicationId": "${DEV_APPLICATION_ID}" // refeer to environment variable DEV_APPLICATION_ID
    },
    "executors": {
        "defPrefix": "?", // default bot prefix, which used to invoke all commands
        "queuePaginationMaxElmsOnPage": 20 // max elements in embed message on single page
    },
    "sequencers": {
        "maxInactivityTimeMinutes": 5, // max inactivity time, after bot leaving voice channel (if less than 0, not leave)
        "maxInactivityTimeAfterPauseTrackMinutes": 10, // max inactivity time (also empty channel) after bot leave
        "maxVotingElapseTimeMinutes": 2, // max time in which to conduct the vote (if less than 0, no maximum time)
        "activityStatusSequencer": {
            "enableRandomizeActivityStatus": true, // enable/disable changing after X time bot activity status
            "intervalSeconds": 10 // change activity status interval (in seconds)
        }
    },
    "miscellaneous": {
        "logger": {
            "loggerEnabled": true, // enable/disable logger
            "loggerOutputFolderName": "dev-logs", // logger output directory
            "loggerSensitivity": [ // saving information based on array parameters
                "INFO", "WARN", "ERROR", "DEBUG"
            ],
            "enableLoggedToStandardOutput": true, // enable/disable logging values in console
            "enableLoggedToFileOutput": true // enable/disable save logs into .log files
        }
    },
    "database": {
        "databaseUrl": "${DEV_DATABASE_CONNECTION_STRING}", // refeer to environment variable DEV_DATABASE_CONNECTION_STRING
        "enforceSslConnection": true, // force ssl connection (only for production mode)
        "username": "${DEV_DATABASE_USERNAME}", // refeer to environment variable DEV_DATABASE_USERNAME
        "password": "${DEV_DATABASE_PASSWORD}", // refeer to environment variable DEV_DATABASE_PASSWORD
        "createDatabaseIfNotExist": true, // create database, if does not exist
        "sqlOnStandardOutput": true, // print JDBC sql commands on console
        "hibernateDriverPackage": "[external database driver package, ex. com.mysql.cj.jdbc.Driver]",
        "hibernateDialectPackage": "org.hibernate.dialect.[selected database dialect]",
        "hbm2ddlAutoMode": "none" // hibernate db management (DONT USE AUTO!!!, use NONE/VALIDATE)
    }
}
```

> NOTE: By default, the application saves logs in the `/target` directory, while in the production (.jar) version this will be the folder in which the application will be launched.
* To run the application in the development mode, use the `--dev` switch. Running the application without any arguments will load the production version configuration file.
```
$ java --dev FranekBot
```

<a name="running-in-the-background-daemon"></a>
## Running in the background (daemon)
In order to run the programme as a separate server process (on a machine running UNIX, for example Linux), it is recommended to use a daemon. You can read how to use the programme on the [official website](https://manpages.ubuntu.com/manpages/kinetic/en/man1/daemon.1.html). Alternatively, you can run the programme as a separate process and save the PID to a file. To do this, use the command:
```
$ nohup java -Xmx512m -jar franek-bot_1.0-SNAPSHOT-jar-with-dependencies.jar </dev/null 2>&1 | tee logfile.log &
```
> NOTE: To return to the shell, use the `Ctrl+C` or `⌘+C`.

<a name="jar-package"></a>
## JAR package
The application has been prepared to make package of this application into a JAR executable file. All you need to do is run the `clean` option in the IDE and then `package` in the `Maven` tab. To run the packaged jar file, use the following command:
```
$ java -jar -Xmx512m franek-bot_1.0-SNAPSHOT-jar-with-dependencies.jar
```

<a name="author"></a>
## Author
Created by Miłosz Gilga. If you have any questions about the application send message: [personal@miloszgilga.pl](mailto:personal@miloszgilga.pl).

<a name="license"></a>
## License
This application is on MIT License.
