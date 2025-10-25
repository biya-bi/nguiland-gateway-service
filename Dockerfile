# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jre-alpine

WORKDIR /opt/ostock

COPY target/*.jar ./gateway-service.jar

ENTRYPOINT java -jar ./gateway-service.jar
