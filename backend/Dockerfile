FROM gcr.io/distroless/java25-debian13

WORKDIR /app

COPY build/extracted/lib ./lib
COPY build/extracted/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
