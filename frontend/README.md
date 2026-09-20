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

The app will be available at `http://localhost:4200` and proxies API calls to the backend at `http://localhost:8080`.

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
| `apiUrl`                | `http://localhost:8080`      | Base URL of the backend API                |
| `adminGroupName`        | `Admin`                      | OIDC group name that grants admin access   |
| `fullAccessGroupNames`  | `['Stúdiós', 'Öregstúdiós']` | OIDC groups with full access               |
| `defaultOwnerName`      | `SVIE`                       | Pre-selected owner when creating a device  |
| `defaultDeviceStatus`   | `GOOD`                       | Pre-selected status when creating a device |
| `defaultDeviceQuantity` | `1`                          | Pre-filled quantity when creating a device |

Authentication is handled via OIDC (Authentik) using `angular-auth-oidc-client`.

### Production

The Docker image builds the app with the `production` configuration and serves the static files with nginx on port `8080` (see [`Dockerfile`](Dockerfile) and [`nginx/default.conf`](nginx/default.conf)).
