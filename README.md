<img align="right" src="https://raw.githubusercontent.com/Milosz08/JWizard_Discord_Bot/master/gfx/github-logo.png" height="160">

# JWizard Discord Bot

[![Generic badge](https://img.shields.io/badge/Made%20in-Java%20SE%2017-1abc9c.svg)](https://www.java.com/en/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Build%20with-Gradle-green.svg)](https://gradle.org/)&nbsp;&nbsp;
[![Generic badge](https://img.shields.io/badge/Packaging-Fat%20jar-brown.svg)](https://gradle.org/)&nbsp;&nbsp;
<br>

A multi-functional music bot (likely to be enhanced with additional features over time) that allows you to play, pause, repeat and queue songs on the popular VoIP Discord platform. Written entirely using Java SE 17 and Spring Framework IoC container.

## Table of content
* [Multiple versions separation](#multiple-versions-separation)
* [Clone script](#clone-script)
* [Run from IDE](#run-from-ide)
* [Run from JAR](#run-from-jar)
* [Change Xmx and Xms parameters](#change-xmx-and-xms-parameters)
* [Internationalization (i18n)](#internationalization-i18n)
* [Author](#author)
* [Project status](#project-status)
* [License](#license)

<a name="multiple-versions-separation"></a>
## Multiple versions separation
This application was split into a production and development version. There are different configuration files for the two versions. For the development version it is `properties-dev.yml` and for the production version it is `properties-prod.yml`.
With this configuration, it's possible to run the production version on the server, and the development version on the computer, where new functionalities can be added and tested (continuity of service).
> NOTE: The production and development versions are actually separate instances of discord bots. Two separate applications must be created for them to work properly.

<a name="clone-script"></a>
## Clone script
To install the program on your computer use the command (or use the built-in GIT system in your IDE environment):
```
$ git clone https://github.com/Milosz08/JWizard_Discord_Bot
```

<a name="run-from-ide"></a>
## Run from IDE
1. Before run the application, change `.env.sample` file to `.env` and fill with the correct values:
```properties
# discord integrations
DEV_TOKEN           = xxxxx <- discord api token (available in discord api dashboard)
DEV_APP_ID          = xxxxx <- discord application identifier (available in discord api dashboard)
PROD_TOKEN          = xxxxx
PROD_APP_ID         = xxxxx

#database
DEV_DB_JDBC         = xxxxx <- ex. jdbc:[dbClient]://[dbServer]:[dbPort]/[dbName]
DEV_DB_USERNAME     = xxxxx <- database username
DEV_DB_PASSWORD     = xxxxx <- database password
PROD_DB_JDBC        = xxxxx
PROD_DB_USERNAME    = xxxxx
PROD_DB_PASSWORD    = xxxxx
```
2. Optionally, you can change the bot's configuration values in the `properties-prod.yml` or `properties-dev.yml` file for the production or development version, respectively.
3. To run application via gradle wrapper, type:
```
$ ./gradlew run --args="--mode=dev"    # for development version
$ ./gradlew run --args="--mode=prod"   # for production version
```

<a name="run-from-jar"></a>
## Run from JAR
> NOTE: At this moment, I don't provide the JAR archive. The JAR archive along with the configuration files will be made available in a stable release.
1. Before creating JAR package, make sur, that `.env` file exist and has been filled with appropriet values. If isn't, do 1 task in `Prepare and run` section.
2. To create fat JAR package via gradle script, run `shadowJar` task:
```
$ ./gradlew shadowJar
```
3. All generated files should be located in `/build/shadow`. You can move this files into selected directory.
4. Optionally, you can change the bot's configuration values in the `properties-prod.yml` or `properties-dev.yml` file for the production or development version, respectively.
5. (FOR UNIX SYSTEMS) To run JAR file, type:
```
$ ./run-dev.sh   # for development version (loading configuration from properties-dev.yml file)
$ ./run-prod.sh  # for production version (loading configuration from properties-prod.yml file)
```
6. (FOR WINDOWS/OTHERS) To run JAR file, type:
```
$ python run-dev.py    # for development version (loading configuration from properties-dev.yml file)
$ python run-prod.py   # for production version (loading configuration from properties-prod.yml file)
```
> NOTE: To run the script, you must have installed Python interpreter 3.11.2 or above. To check Python version, type `python --version` in your command prompt.

<a name="change-xmx-and-xms-parameters"></a>
## Change Xmx and Xms parameters
For bash script file, change this lines of code:
```bash
START_JAVA_HEAP_SIZE="256m"     # -Xms parameter, min. 128MB
MAX_JAVA_HEAP_SIZE="512m"       # -Xmx parameter

```
For Python script file, change this lines of code:
```python
start_java_heap_size    = '256m' # -Xms parameter, min. 128MB, recommended 256MB
max_java_heap_size      = '512m' # -Xmx parameter
```
Java heap size configuration is the same for both configuration (development and production).

<a name="internationalization-i18n"></a>
## Internationalization (i18n)
1. To add a new language, create a new resource file in the `/lang` directory with a name contains the language prefix, ex. `messages_fr.properties`, copy all keys from `messages_en_us.properties` and change messages into corresponding to the selected language.
2. To set the language, change this property in `properties-dev.yml` or `properties-prod.yml`:
```yml
bot:
  misc:
    locale:
      selected-locale: fr
```

<a name="author"></a>
## Author
Created by Miłosz Gilga. If you have any questions about this application, send message: [personal@miloszgilga.pl](mailto:personal@miloszgilga.pl).

<a name="project-status"></a>
## Project status
Project is still in development.

<a name="license"></a>
## License
This application is on MIT License.
