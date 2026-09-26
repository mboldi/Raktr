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
import { getRuntimeConfig } from './runtime-config';

/**
 * Starts Sentry error reporting when a DSN is configured. Tracing, profiling and session
 * replay are deliberately left out: the frontend reports errors only.
 */
export function initSentry(): void {
  const { sentryDsn, sentryEnvironment, release } = getRuntimeConfig();

  if (!sentryDsn) {
    return;
  }

  Sentry.init({
    dsn: sentryDsn,
    environment: sentryEnvironment,
    release,
    beforeSend: (event, hint) => (isRequestNoise(hint.originalException) ? null : event),
  });
}

/** Routes Angular's errors to Sentry and keeps the reported user in sync with the session. */
export function provideSentry(): (Provider | EnvironmentProviders)[] {
  return [
    { provide: ErrorHandler, useValue: createErrorHandler() },
    provideEnvironmentInitializer(() => {
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
