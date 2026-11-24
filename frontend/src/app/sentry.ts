import { HttpErrorResponse } from '@angular/common/http';
import {
  EnvironmentProviders,
  ErrorHandler,
  inject,
  Provider,
  provideEnvironmentInitializer,
} from '@angular/core';
import * as Sentry from '@sentry/angular';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { getRuntimeConfig, RuntimeConfig } from './runtime-config';

const privateHeaders = {
  deny: ['forwarded', '-ip', 'remote-', 'via', '-user'],
};

/** Starts Sentry error reporting and navigation tracing when a DSN is configured. */
export function initSentry(): void {
  const config = getRuntimeConfig();

  if (!config.sentryDsn) {
    return;
  }

  Sentry.init(sentryOptions(config));
}

export function sentryOptions({
  sentryDsn,
  sentryEnvironment,
  release,
}: RuntimeConfig): Sentry.BrowserOptions {
  return {
    dsn: sentryDsn,
    environment: sentryEnvironment,
    release,
    // The SDK collects every category by default; the same baseline as request-manager.
    dataCollection: {
      cookies: false,
      databaseQueryData: false,
      genAI: { inputs: false, outputs: false },
      graphQL: { document: false, variables: false },
      httpBodies: [],
      httpHeaders: { request: privateHeaders, response: privateHeaders },
      urlQueryParams: false,
      userInfo: false,
    },
    // Same-origin /api calls carry the trace on to the backend by default.
    integrations: [Sentry.browserTracingIntegration()],
    tracesSampleRate: 0.3,
    beforeSend: (event, hint) => (isRequestNoise(hint.originalException) ? null : event),
  };
}

/**
 * Routes Angular's errors to Sentry, names navigation spans after the routes and keeps the
 * reported user in sync with the session.
 */
export function provideSentry(): (Provider | EnvironmentProviders)[] {
  return [
    { provide: ErrorHandler, useValue: createErrorHandler() },
    provideEnvironmentInitializer(() => {
      inject(Sentry.TraceService);
      inject(OidcSecurityService).userData$.subscribe(({ userData }) => {
        Sentry.setUser(
          userData ? { id: userData['sub'], username: userData['preferred_username'] } : null,
        );
      });
    }),
  ];
}

/**
 * Sentry's own handler unwraps an HttpErrorResponse into a plain string before it hands the
 * error to the SDK, so by the time `beforeSend` runs there is nothing left to recognise. The
 * noise therefore has to be dropped here, while the error is still intact; `beforeSend` keeps
 * the same guard for what reaches Sentry through the browser's global handlers instead.
 */
function createErrorHandler(): ErrorHandler {
  const sentryErrorHandler = Sentry.createErrorHandler();

  return {
    handleError(error: unknown): void {
      const original = unwrapZoneError(error);

      if (isRequestNoise(original)) {
        // Sentry's handler logs what it reports, so keep the console output for what it doesn't.
        console.error(original);
        return;
      }

      sentryErrorHandler.handleError(error);
    },
  };
}

/** Angular wraps errors thrown inside a zone task, keeping the original under `ngOriginalError`. */
function unwrapZoneError(error: unknown): unknown {
  return (error as { ngOriginalError?: unknown } | null | undefined)?.ngOriginalError ?? error;
}

/**
 * A 4xx is the backend's verdict on a request the user made, and status 0 is a request that
 * was cancelled or never left the browser. Neither says anything about a frontend bug; real
 * server failures (5xx) still get reported, and the backend reports its own side of them.
 */
function isRequestNoise(exception: unknown): boolean {
  return exception instanceof HttpErrorResponse && exception.status < 500;
}
