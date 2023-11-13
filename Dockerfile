FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY --from=build /app/target/shaden-0.0.1-SNAPSHOT.jar /app

CMD ["java", "-jar", "shaden-0.0.1-SNAPSHOT.jar"]
