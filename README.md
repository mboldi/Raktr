# Raktr

Inventory and rental management system for Budavári Schönherz Stúdió (BSS).

This repository contains both halves of the application:

- [`backend/`](backend) — Spring Boot REST API (Java 25, PostgreSQL)
- [`frontend/`](frontend) — Angular single-page app

Each has its own README with setup, build, and configuration details.

## Quick Start

Run the full stack (frontend, backend, and database) with Docker Compose:

```bash
docker compose up --build
```

- Everything: `http://localhost:80` — a proxy container routes `/api` to the backend and the
  rest to the frontend, so the stack is served under one host like it is in production.
  See [`deploy/local-proxy.conf`](deploy/local-proxy.conf).
- Backend directly: `http://localhost:8080` (Swagger UI at `/swagger-ui.html`)
- PostgreSQL: `localhost:5432`

## Development

For local development without Docker, run the backend and frontend separately — see [`backend/README.md`](backend/README.md) and [`frontend/README.md`](frontend/README.md).

### Git hooks

The repo ships a pre-commit hook in [`.githooks/`](.githooks) that runs the formatting and lint
checks CI would run, but only for the side of the tree you touched — `spotlessCheck` for staged
Java, Prettier and ESLint for staged frontend files. It takes a few seconds and runs no tests.

Enable it once per clone:

```bash
git config core.hooksPath .githooks
```

It skips a side whose toolchain is missing (`java` or `pnpm` not on `PATH`) and says so. To
commit past it, use `git commit --no-verify`.

## CI/CD

Images are published to `ghcr.io/mboldi/raktr/backend` and `ghcr.io/mboldi/raktr/frontend`.
