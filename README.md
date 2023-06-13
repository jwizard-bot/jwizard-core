<img align="right" src="https://raw.githubusercontent.com/Milosz08/JWizard_Discord_Bot/master/gfx/github-logo.png" height="160">

# JWizard Discord Bot

![](https://img.shields.io/badge/Made%20in-Java%20SE%2017-1abc9c.svg)
&nbsp;&nbsp;
![](https://img.shields.io/badge/Build%20with-Gradle-green.svg)
&nbsp;&nbsp;
![](https://img.shields.io/badge/Packaging-Fat%20jar-brown.svg)
&nbsp;&nbsp;
<br>
> More info about this project you will find [on my personal website](https://miloszgilga.pl/project/jwizard).

A multi-functional rich discord bot (likely to be enhanced with additional features over time). Modularity with the ability
to add new functionality quite (not)easily (yeah, in case you forgot it's Java bro :), management through a web interface.
It is divided into several parts:
* music module (online source player)
* statistics collection system
* music player playlist recording system
* voting system.

Written mostly in Java SE 17 together with JDA, Spring Context (DAPI), Spring Boot (WebAPI) and many others dependencies.
Runtime scripts prepared with Bash (for UNIX) and Python (for WINDOWS/OTHERS)

This application required newest stable [JMPS library](https://github.com/Milosz08/jmpsl). It may happen that the version
of JMPSL you are currently using is not yet available in the remote repository. In this case, you will have to build the
library yourself in your local maven repository (see below).

## Table of content
* [Contribute this project](#contribute-this-project)
* [Multiple versions separation](#multiple-versions-separation)
* [Clone script](#clone-script)
* [Prepare project](#prepare-project)
* [Run with Docker containers](#run-with-docker-containers)
* [Run from IDE](#run-from-ide)
* [Run from JAR](#run-from-jar)
* [Change Xmx and Xms parameters (JVM Heap Size)](#change-xmx-and-xms-parameters)
* [Internationalization (i18n)](#internationalization-i18n)
* [Tech stack](#tech-stack)
* [Author](#author)
* [Project status](#project-status)
* [License](#license)

<a name="contribute-this-project"></a>
## Contribute this project
If you have the desire and time to help me in writing this bot, you can write to me in a private message or at
[personal@miloszgilga.pl](mailto:personal@miloszgilga.pl). However, please note that in order to be a contributor to
this application, you must have knowledge in advanced object-oriented programming techniques and experienced in advanced
Java programming (lambda expressions and functional interfaces, spring, rest, cache, multi modules and websockets). If
you do not feel confident as a contributor, you can always submit a request for adding new functionality.

<a name="multiple-versions-separation"></a>
## Multiple versions separation
This application was split into a production and development version. There are different configuration files for the two
versions. For the development version it is `properties-dev.yml` and for the production version it is `properties-prod.yml`.
With this configuration, it's possible to run the production version on the server, and the development version on the
computer, where new functionalities can be added and tested (continuity of service).
> NOTE: The production and development versions are actually separate instances of discord bots. Two separate applications
> must be created for them to work properly.

<a name="clone-script"></a>
## Clone script
To install the program on your computer use the command (or use the built-in GIT system in your IDE environment):
```
$ git clone https://github.com/Milosz08/jwizard jwizard-discord-bot
```

<a name="prepare-project"></a>
## Prepare project
Before run the project you must download and build the latest version of the JMPS library. To do this, follow the steps below:
* Clone the latest version of JMPS library from my GitHub:
```
$ git clone -b [latest branch, ex. v1.0.2_05] https://github.com/Milosz08/jmpsl jmps-library
```
* Go to root of project and type:
```
$ ./gradlew packageAllToLocal
```
This command build all library modules in `~/.m2/repositories/pl/miloszgilga/jmpsl` directory.
* Now, after you clone AirHub Master API project via command in `Clone script` section, make sure than all repository
  paths are typed correctly (in `/gradle/libs.versions.toml` file):
```toml
[versions]
# ...
jmpsl = '[version from branch WITHOUT "v" character, ex. 1.0.2_05]'
# ...

[libraries]
# ...
jmpsl-core          = { module = 'pl.miloszgilga:jmpsl-core',           version.ref = 'jmpsl' }
jmpsl-security      = { module = 'pl.miloszgilga:jmpsl-security',       version.ref = 'jmpsl' }
# ...
```
* Create `.env` file and put necessary values (from `.env.sample` file) (you must be in ROOT of project context):
```
$ grep -vE '^\s*$|^#' .env.sample > .env
$ nano .env
```
Example `.env` file:
```properties
# discord authentication api
PROD_TOKEN                      = xxxxx <- discord api token (available in discord api dashboard)
PROD_APP_ID                     = xxxxx <- discord application identifier (available in discord api dashboard)
DEV_TOKEN                       = xxxxx <- discord api token (available in discord api dashboard)
DEV_APP_ID                      = xxxxx <- discord application identifier (available in discord api dashboard)

# database connectivity
PROD_DB_JDBC                    = xxxxx <- ex. jdbc:[dbClient]://[dbServer]:[dbPort]/[dbName]
PROD_DB_USERNAME                = xxxxx <- database username
PROD_DB_PASSWORD                = xxxxx <- database password

# web api
PROD_BASE_URL                   = <base backend (api) app url, ex. api.jwizard.com>
PROD_FRONTEND_CORS_AGENT        = <base frontend app url, ex. jwizard.com>
PROD_JWT_KEY                    = <JWT salt pseudogenerated random string>
PROD_FRONTEND_WITH_JWT_ISSUER   = <JWT issuer, ex. jwizard.com>
```
* Optionally, you can change the bot's configuration values in the `properties-prod.yml` or `properties-dev.yml` file 
for the production or development version, respectively.
* Congrats, now you can build the project via:
```
./gradlew build
```
If it fails, make sure the paths specified are correct and that the `.jar` JMPSL files exist in the local maven repository.

<a name="run-with-docker-containers"></a>
## Run with Docker containers
* Before run application with Docker containers technology, insert these environment variables in `.env` file:
```properties
DOCKER_DB_NAME                          = xxxxx <- docker database name
DOCKER_DB_USERNAME                      = xxxxx <- docker database user (by default 'root')
DOCKER_DB_PASSWORD                      = xxxxx <- docker database password (must be set)
DOCKER_DB_MYSQL_ROOT_PASSWORD           = xxxxx <- root password for MySQL service
```
* Build and dockerized application in `development` mode (embed H2 database) via:
```
$ ./docker-assembly.sh -Dmode=dev
```
By default, application should be available on `http://127.0.0.1:8085`.
* Build and dockerized application in `production` mode (additional containers: mysql and phpmyadmin) via:
```
$ ./docker-assembly.sh -Dmode=prod
```
By default, application should be available on `http://127.0.0.1:9095`.<br>
Phpmyadmin should be available on `http://127.0.0.1:8080`.

You can optionally provide `-Dxms` and `-Dxmx` parameters for controlling JVM heap. By default it's: Xms=512m, Xmx=1024m.

<a name="run-from-ide"></a>
## Run from IDE
To run application via gradle wrapper, type:
```
$ ./gradlew bootRunDev    # for development version
$ ./gradlew bootRunProd   # for production version
```

<a name="run-from-jar"></a>
## Run from JAR
> NOTE: At this moment, I don't provide the JAR archive. The JAR archive along with the configuration files will be made available in a stable release.
1. Before creating JAR package, make sur, that `.env` file exist and has been filled with appropriet values. If isn't, do 1 task in `Run from IDE` section.
2. To create fat JAR package via gradle script, run `bootJar` task:
```
$ ./gradlew bootJar
```
3. All generated files should be located in `/build/jar`. You can move this files into selected directory.
4. Optionally, you can change the bot's configuration values in the `properties-prod.yml` or `properties-dev.yml` file for the production or development version, respectively.
5. To run JAR file, type:
* (FOR UNIX SYSTEMS):
```
$ ./run-dev.sh   # for development version (loading configuration from properties-dev.yml file)
$ ./run-prod.sh  # for production version (loading configuration from properties-prod.yml file)
```
* (FOR WINDOWS/OTHERS):
```
> python run-dev.py    # for development version (loading configuration from properties-dev.yml file)
> python run-prod.py   # for production version (loading configuration from properties-prod.yml file)
```
> NOTE: To run the script, you must have installed Python interpreter 3.11.2 or above. To check Python version, type 
> `python --version` in your command prompt.

Application will run in the background with assigned PID.
6. Too stop the application, execute killing script (only for UNIX servers environments):
```
$ ./kill-dev.sh       # for development version
$ ./kill-prod.sh      # for production version
```

<a name="change-xmx-and-xms-parameters"></a>
## Change Xmx and Xms parameters (JVM Heap Size)
For bash script file, change this lines of code:
```bash
#!/bin/bash

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
1. To add a new language, create a new resource file in the `classpath:/i18n-api` and `classpath:/i18n-jda` directories
via command:
* (FOR UNIX SYSTEMS):
```
$ cd run-scripts
$ ./lang-gen.sh --lang=[i18n tag]
```
* (for WINDOWS/OTHERS):
```
> cd run-scripts
> python lang-gen.py --lang=[i18n tag]
```
where `[i18n tag]` is one of the internationalization standards tag (ex. `en-US`, `fr`, `pl` etc.)
> NOTE: To run the script, you must have installed Python interpreter 3.11.2 or above. To check Python version, type `python --version` in your command prompt.
2. After successfully generated message resources file, fill keys with properly values.
> NOTE: If you not provide parameter, application take key as value.
3. (FOR JDA INTERFACE) To set the language, change this property in `properties-dev.yml` or `properties-prod.yml`:
```yml
bot:
  misc:
    locale:
      selected-locale: [i18n tag]
```
4. (FOR WEB API INTERFACE) To set the language, change this property in `classpath:/application.yml`:
```yml
jmpsl:
    core:
        locale:
            available-locales: en_US,pl,[i18n tag]
            default-locale: [i18n tag]
```
* To force language in HTTP requests, be sure that `Accept-Language` header was added in every requests, ex.:
```
Accept-Language: fr
```
Without this header, server will return responses in the language you have set as default in the `jmpsl.core.locale.default-locale`
parameter.

<a name="tech-stack"></a>
## Tech stack
* Java 17
* JDA, JDA Utilites, Lavaplayer
* JMPSL (Core, Communication, Security, OAuth2)
* Spring Boot (IoC container for DAPI runtime, WebAPI for web client interface)
* Spring Data JPA (Hibernate, JPA, MySQL database, H2 database)
* Spring Cache (implemented by EhCache, DAPI runtime)
* (soon) React (web client - guild modules management interface)
* Docker technology

<a name="author"></a>
## Author
Created by Miłosz Gilga. If you have any questions about this application, send message: [personal@miloszgilga.pl](mailto:personal@miloszgilga.pl).

<a name="project-status"></a>
## Project status
Project is still in development.

<a name="license"></a>
## License
This application is on Apache 2.0 License.
