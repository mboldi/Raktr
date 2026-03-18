# Raktr

Inventory and rental management REST API for Budavári Schönherz Stúdió (BSS).

## Getting Started

### Prerequisites

- Java 25+
- PostgreSQL (or Docker)

### Database Setup

Start a PostgreSQL instance:

```bash
docker run -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=raktrdb -p 5432:5432 -d postgres:18-alpine
```

### Run

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`. Swagger UI is at `/swagger-ui.html`.

### Docker Compose

To run the full stack:

```bash
docker compose up --build
```

This starts both the application and a PostgreSQL database.

## Build

```bash
./gradlew build       # build and run tests
./gradlew bootJar     # create JAR artifact
```

## Testing

Unit tests and integration tests are separated into distinct source sets.

```bash
./gradlew test              # unit tests only
./gradlew integrationTest   # integration tests only (requires Docker)
./gradlew check             # both
```

Integration tests use TestContainers to spin up a PostgreSQL instance automatically — Docker must be running.

## Docker

There are two Dockerfiles:

- **`Dockerfile`** — lightweight production image that expects a pre-built, extracted JAR. Used in CI.
- **`Dockerfile.local`** — multi-stage build that compiles the JAR inside Docker. No prerequisites other than Docker
  itself.

`docker compose up --build` uses `Dockerfile.local` by default.

## Configuration

Key settings in `application.yml`:

| Property                                                | Default                                    |
|---------------------------------------------------------|--------------------------------------------|
| `spring.datasource.url`                                 | `jdbc:postgresql://localhost:5432/raktrdb` |
| `spring.datasource.username`                            | `postgres`                                 |
| `spring.datasource.password`                            | `postgres`                                 |
| `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` | Authentik OIDC JWK Set URI                 |
| `spring.security.oauth2.resourceserver.jwt.issuer-uri`  | Authentik OIDC Issuer                      |

### Production Profile

The `prod` profile disables Swagger UI and API docs. Activate it by setting:

```bash
SPRING_PROFILES_ACTIVE=prod
```

Or pass it as a JVM argument:

```bash
java -jar app.jar --spring.profiles.active=prod
```