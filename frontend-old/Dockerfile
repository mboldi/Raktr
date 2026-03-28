FROM node:24-alpine AS build

USER node
WORKDIR /home/node/app

# install dependencies (cached unless package files change)
COPY --chown=node package.json package-lock.json ./
RUN npm install --no-fund --no-audit --loglevel warn

# build the Angular app in production mode
COPY --chown=node . .
RUN npm run build -- --configuration production

FROM nginx:alpine-slim AS runtime

# remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# copy custom nginx config and built app
COPY nginx/default.conf /etc/nginx/conf.d/
COPY --from=build /home/node/app/dist /usr/share/nginx/html

# run on non-privileged port
EXPOSE 8080

# run nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
