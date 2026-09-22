// Browser runtime configuration. The container rewrites this file from its environment on
// startup (see nginx/40-sentry-config.sh); the values below are the defaults used by
// `ng serve` and by an image started without those variables. Empty DSN keeps Sentry off.
window.raktrConfig = {
  sentryDsn: '',
  sentryEnvironment: 'local',
  release: 'frontend@local',
};
