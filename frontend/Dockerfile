FROM node:22-alpine as build

# change user from root to rootless node user
USER node
RUN mkdir /home/node/app
RUN chown node /home/node/app
WORKDIR /home/node/app

# install project dependencies
COPY --chown=node ./package*.json ./
ARG INSTALL_ARGS="--no-fund --no-audit --loglevel warn"
RUN npm clean-install $INSTALL_ARGS

# build the application
COPY --chown=node ./ ./
RUN npm run build

FROM nginx:latest
# copy the compiled application to the webserver
COPY proxy-conf/default.conf /etc/nginx/conf.d
COPY --from=build /home/node/app/dist /usr/share/nginx/html
