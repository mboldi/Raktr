#!/bin/sh
# Rewrites the browser runtime config from the container environment before nginx starts.
# An empty SENTRY_DSN leaves Sentry switched off, same as the backend.
set -e

cat > /usr/share/nginx/html/config.js <<CONFIG
window.raktrConfig = {
  sentryDsn: '${SENTRY_DSN:-}',
  sentryEnvironment: '${SENTRY_ENVIRONMENT:-production}',
  release: '${RAKTR_RELEASE:-frontend@unknown}',
};
CONFIG
