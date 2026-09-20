# Raktr frontend

Inventory and rental management frontend application for Budavári Schönherz Stúdió (BSS).

This is the frontend: an Angular single-page app that talks to the [backend](../backend) API.

## Getting Started

### Prerequisites

- Node.js 24.15+ (Angular 22 requires it) — the major is pinned in `.node-version`
- pnpm — `corepack enable pnpm` picks up the version pinned in `package.json`
- The backend running (see [`../backend/README.md`](../backend/README.md))

### Run

```bash
pnpm install
pnpm start
```

The app will be available at `http://localhost:4200`. Requests to `/api` are proxied to the
backend at `http://localhost:8080` by [`proxy.conf.json`](proxy.conf.json), which strips the
`/api` prefix before forwarding.

### Docker Compose

To run the full stack (frontend, backend, and database):

```bash
docker compose up --build
```

## Build

```bash
pnpm build    # production build, output in dist/frontend
pnpm watch    # development build, rebuilds on change
```

## Testing

Currently, there are no automated tests in this part of the project.

## Configuration

Environment-specific settings live in [`src/environments/environment.ts`](src/environments/environment.ts):

| Property                | Default                      | Description                                |
| ----------------------- | ---------------------------- | ------------------------------------------ |
| `apiUrl`                | `/api`                       | Base path of the backend API, same origin  |
| `adminGroupName`        | `Admin`                      | OIDC group name that grants admin access   |
| `fullAccessGroupNames`  | `['Stúdiós', 'Öregstúdiós']` | OIDC groups with full access               |
| `defaultOwnerName`      | `SVIE`                       | Pre-selected owner when creating a device  |
| `defaultDeviceStatus`   | `GOOD`                       | Pre-selected status when creating a device |
| `defaultDeviceQuantity` | `1`                          | Pre-filled quantity when creating a device |

Authentication is handled via OIDC (Authentik) using `angular-auth-oidc-client`. Access tokens
are attached to requests under `/api` only.

### Same-origin API

The app calls the backend at `/api` on its own origin, so it carries no environment-specific
configuration and the same image runs anywhere. Routing `/api` to the backend and everything
else to the frontend is the deployment's job; under Docker Compose that is
[`../deploy/local-proxy.conf`](../deploy/local-proxy.conf).

Both strip the `/api` prefix, since the backend serves its routes at `/v1/...`. If the backend
ever moves under `/api` itself, drop the prefix rewrite from all three places instead.

### Production

The Docker image builds the app with the `production` configuration and serves the static files with nginx on port `8080` (see [`Dockerfile`](Dockerfile) and [`nginx/default.conf`](nginx/default.conf)).
