FROM node:24-alpine AS build

ARG VERSION=local

ENV COREPACK_ENABLE_DOWNLOAD_PROMPT=0
ENV SENTRY_RELEASE=frontend@${VERSION}
RUN corepack enable pnpm

USER node
WORKDIR /home/node/app

# install dependencies (cached unless package files change)
COPY --chown=node package.json pnpm-lock.yaml pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile

# build the Angular app in production mode
COPY --chown=node . .
RUN pnpm build --configuration production

# upload the hidden source maps so stack traces are readable, then drop them from the output.
# Without the token (local builds, pull requests) the upload is skipped.
RUN --mount=type=secret,id=SENTRY_AUTH_TOKEN,uid=1000 \
    if [ -s /run/secrets/SENTRY_AUTH_TOKEN ]; then \
        SENTRY_AUTH_TOKEN=$(cat /run/secrets/SENTRY_AUTH_TOKEN) pnpm sentry:sourcemaps; \
    fi && \
    find dist/frontend/browser -name '*.map' -delete

FROM nginx:alpine-slim AS runtime

ARG VERSION=local

# the release the uploaded source maps were built for; handed to the browser by config.js
ENV RAKTR_RELEASE=frontend@${VERSION}

# remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# copy custom nginx config and built app
COPY nginx/default.conf /etc/nginx/conf.d/
COPY --chmod=755 nginx/40-sentry-config.sh /docker-entrypoint.d/40-sentry-config.sh
COPY --from=build /home/node/app/dist/frontend/browser /usr/share/nginx/html

# run on non-privileged port
EXPOSE 8080

# run nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
