export interface RuntimeConfig {
  /** Sentry DSN; empty means error reporting is disabled. */
  sentryDsn: string;
  sentryEnvironment: string;
  release: string;
}

declare global {
  interface Window {
    raktrConfig?: Partial<RuntimeConfig>;
  }
}

const defaults: RuntimeConfig = {
  sentryDsn: '',
  sentryEnvironment: 'local',
  release: 'frontend@local',
};

/** Reads the config public/config.js puts on `window`, falling back to the defaults. */
export function getRuntimeConfig(): RuntimeConfig {
  return { ...defaults, ...window.raktrConfig };
}
