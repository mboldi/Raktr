FROM eclipse-temurin:25-jdk-noble AS build

ARG VERSION=local

WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
COPY buildSrc ./buildSrc

RUN ./gradlew --no-daemon --version

COPY src ./src

RUN ./gradlew bootJar --parallel --no-daemon -Pversion=$VERSION

RUN java -Djarmode=tools -jar build/libs/*.jar extract --destination build/extracted

FROM gcr.io/distroless/java25-debian13

WORKDIR /app

COPY --from=build /app/build/extracted/lib ./lib
COPY --from=build /app/build/extracted/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
