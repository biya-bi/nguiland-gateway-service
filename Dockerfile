# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jre-alpine

WORKDIR /opt/ostock

COPY target/*.jar ./gateway-service.jar

COPY certs/* /etc/ssl/certs/

RUN keytool -import -alias nguiland -keystore /opt/java/openjdk/lib/security/cacerts -file /etc/ssl/certs/__nguiland_org.crt -noprompt

ENTRYPOINT java -jar ./gateway-service.jar
