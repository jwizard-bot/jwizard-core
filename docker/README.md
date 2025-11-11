# JWizard Core

JWizard is an open-source Discord music bot that manages audio content from various multimedia
sources and features an innovative web player. This repository contains the core of the application,
which supports Discord API event handlers and a message broker for handling events from the web
interface. It is also designed for clustering, utilizing a shard-offset system.

[Webpage](https://jwizard.pl)
| [GitHub repository](https://github.com/jwizard-bot/jwizard-core)
| [Support](https://github.com/sponsors/jwizard-bot)

### Clustering system example with multiple concurrent instance

```
instance 0              instance 1       ...     instance N
├─ process 0            ├─ process 0
│  ├─ shards 0-9        │  ├─ shards 0-9
├─ process 1            ├─ process 1
│  ├─ shards 10-19      │  ├─ shards 10-19
│  ...                  │  ...
├─ process N            ├─ process N
```

> NOTE: Min and max shard defining shard offset are inclusive.

More about sharding, clustering multiple concurrent instances and shards fragmentation (different
shard ranges for distributed JVM architecture) you will find here:

* [https://discord.com/developers/docs/events/gateway#sharding](https://discord.com/developers/docs/events/gateway#sharding)
* [https://skelmis.co.nz/posts/discord-bot-sharding-and-clustering](https://skelmis.co.nz/posts/discord-bot-sharding-and-clustering)

## Build image

```bash
docker build \
  --build-arg JWIZARD_VERSION=latest \
  --build-arg JWIZARD_MAVEN_NAME=<maven m2 repository username> \
  --build-arg JWIZARD_MAVEN_SECRET=<maven m2 repository secret> \
  -t milosz08/jwizard-core .
```

## Create container

* Using command:

```bash
# for 2 concurrent instances, one process per instance with 10 shards per process
docker run -d \
  --name jwizard-core \
  -p 8080:8080 \
  -e JWIZARD_VAULT_SERVER=<vault server url> \
  -e JWIZARD_VAULT_USERNAME=<vault username> \
  -e JWIZARD_VAULT_PASSWORD=<vault password> \
  -e JWIZARD_XMS=1024m \
  -e JWIZARD_XMX=1024m \
  -e JWIZARD_JDA_INSTANCE_NAME=core-instance-0 \
  -e JWIZARD_JDA_SHARDING_OFFSET_START=0 \
  -e JWIZARD_JDA_SHARDING_OFFSET_END=9 \
  -e JWIZARD_JDA_SHARDING_TOTAL_SHARDS=10 \
  milosz08/jwizard-core:latest

docker run -d \
  --name jwizard-core \
  -p 8081:8080 \
  -e JWIZARD_VAULT_SERVER=<vault server url> \
  -e JWIZARD_VAULT_USERNAME=<vault username> \
  -e JWIZARD_VAULT_PASSWORD=<vault password> \
  -e JWIZARD_XMS=1024m \
  -e JWIZARD_XMX=1024m \
  -e JWIZARD_JDA_INSTANCE_NAME=core-instance-1 \
  -e JWIZARD_JDA_SHARDING_OFFSET_START=0 \
  -e JWIZARD_JDA_SHARDING_OFFSET_END=9 \
  -e JWIZARD_JDA_SHARDING_TOTAL_SHARDS=10 \
  milosz08/jwizard-core:latest
```

* Using `docker-compose.yml` file:

```yaml
# for 2 concurrent instances, one process per instance with 10 shards per process

services:
  jwizard-core-instance-0:
    container_name: jwizard-core-instance-0
    image: milosz08/jwizard-core:latest
    ports:
      - '8080:8080'
    environment:
      JWIZARD_VAULT_SERVER: <vault server url>
      JWIZARD_VAULT_USERNAME: <vault username>
      JWIZARD_VAULT_PASSWORD: <vault password>
      JWIZARD_XMS: 1024m
      JWIZARD_XMX: 1024m
      JWIZARD_JDA_INSTANCE_NAME: core-instance-0
      JWIZARD_JDA_SHARDING_OFFSET_START: 0
      JWIZARD_JDA_SHARDING_OFFSET_END: 9
      JWIZARD_JDA_SHARDING_TOTAL_SHARDS: 10
    networks:
      - jwizard-network

  jwizard-core-instance-1:
    container_name: jwizard-core-instance-1
    image: milosz08/jwizard-core:latest
    ports:
      - '8081:8080'
    environment:
      JWIZARD_VAULT_SERVER: <vault server url>
      JWIZARD_VAULT_USERNAME: <vault username>
      JWIZARD_VAULT_PASSWORD: <vault password>
      JWIZARD_XMS: 1024m
      JWIZARD_XMX: 1024m
      JWIZARD_JDA_INSTANCE_NAME: core-instance-1
      JWIZARD_JDA_SHARDING_OFFSET_START: 0
      JWIZARD_JDA_SHARDING_OFFSET_END: 9
      JWIZARD_JDA_SHARDING_TOTAL_SHARDS: 10
    networks:
      - jwizard-network

  # other containers...

networks:
  jwizard-network:
    driver: bridge
```

## License

This project is licensed under the Apache 2.0 License.
