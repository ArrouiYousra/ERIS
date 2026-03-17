# syntax=docker/dockerfile:1.2
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY .mvn/ .mvn
COPY mvnw mvnw
RUN chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -DskipTests package && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/app.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
