# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy AS base
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve
COPY src ./src

FROM base AS package
RUN ./mvnw package

FROM eclipse-temurin:17-jre-jammy AS build
WORKDIR /target
ARG JAR_FILE=/app/target/*.jar
COPY --from=package ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre-jammy AS run
ARG OUTPUT_DIR=/target
WORKDIR /opt/app
COPY --from=build ${OUTPUT_DIR}/dependencies/ ./
COPY --from=build ${OUTPUT_DIR}/spring-boot-loader/ ./
COPY --from=build ${OUTPUT_DIR}/snapshot-dependencies/ ./
COPY --from=build ${OUTPUT_DIR}/application/ ./
ENTRYPOINT java org.springframework.boot.loader.launch.JarLauncher