FROM node:24-alpine AS build

ENV COREPACK_ENABLE_DOWNLOAD_PROMPT=0
RUN corepack enable pnpm

USER node
WORKDIR /home/node/app

# install dependencies (cached unless package files change)
COPY --chown=node package.json pnpm-lock.yaml pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile

# build the Angular app in production mode
COPY --chown=node . .
RUN pnpm build --configuration production

FROM nginx:alpine-slim AS runtime

# remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# copy custom nginx config and built app
COPY nginx/default.conf /etc/nginx/conf.d/
COPY --from=build /home/node/app/dist/frontend/browser /usr/share/nginx/html

# run on non-privileged port
EXPOSE 8080

# run nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
