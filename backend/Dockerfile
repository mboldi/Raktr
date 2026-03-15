FROM eclipse-temurin:25-jdk-noble AS build

WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle

RUN ./gradlew --no-daemon --version

COPY src ./src

RUN ./gradlew bootJar --parallel --no-daemon

FROM gcr.io/distroless/java25-debian13

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
