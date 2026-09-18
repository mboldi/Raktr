# Raktr

Inventory and rental management REST API for Budavári Schönherz Stúdió (BSS).

This repository contains both halves of the application:

- [`backend/`](backend) — Spring Boot REST API (Java 25, PostgreSQL)
- [`frontend/`](frontend) — Angular single-page app

Each has its own README with setup, build, and configuration details.

## Quick Start

Run the full stack (frontend, backend, and database) with Docker Compose:

```bash
docker compose up --build
```

- Frontend: `http://localhost:80`
- Backend API: `http://localhost:8080` (Swagger UI at `/swagger-ui.html`)
- PostgreSQL: `localhost:5432`

## Development

For local development without Docker, run the backend and frontend separately — see [`backend/README.md`](backend/README.md) and [`frontend/README.md`](frontend/README.md).

## CI/CD

Images are published to `ghcr.io/mboldi/raktr/backend` and `ghcr.io/mboldi/raktr/frontend`.
