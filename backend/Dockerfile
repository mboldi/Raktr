FROM eclipse-temurin:25-jdk-noble AS build

ARG VERSION=local

WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
COPY buildSrc ./buildSrc

RUN ./gradlew --no-daemon --version

COPY src ./src

# Only caches/ is mounted: the wrapper distribution stays in the layer above, which CI's
# layer cache keeps. The mount itself only persists on local builders.
RUN --mount=type=cache,target=/root/.gradle/caches \
    --mount=type=secret,id=SENTRY_AUTH_TOKEN \
    SENTRY_AUTH_TOKEN=$(cat /run/secrets/SENTRY_AUTH_TOKEN 2>/dev/null || true) \
    ./gradlew assemble --parallel --no-daemon -Pversion=$VERSION

RUN java -Djarmode=tools -jar build/libs/*.jar extract --destination build/extracted

FROM gcr.io/distroless/java25-debian13:nonroot AS runtime

WORKDIR /app

COPY --from=build /app/build/extracted/lib ./lib
COPY --from=build /app/build/extracted/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
