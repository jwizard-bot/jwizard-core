FROM gradle:8.4-jdk11-alpine as builder

USER root

WORKDIR /builder

ADD . /builder

RUN gradle build --stacktrace

FROM openjdk:17-alpine

WORKDIR /app

EXPOSE 8080

COPY --from=builder /builder/build/libs/libs/server.jar .

CMD ["java", "-jar", "server.jar"]
