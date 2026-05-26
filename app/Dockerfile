FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/target/cicd-k8s-app-0.0.1-SNAPSHOT.jar app.jar

ENV APP_VERSION=dev
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
