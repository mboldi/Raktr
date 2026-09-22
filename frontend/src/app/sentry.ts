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
    sendDefaultPii: true,
    beforeSend: (event, hint) => (isRequestNoise(hint.originalException) ? null : event),
  });
}

/** Routes Angular's errors to Sentry and keeps the reported user in sync with the session. */
export function provideSentry(): (Provider | EnvironmentProviders)[] {
  return [
    { provide: ErrorHandler, useValue: Sentry.createErrorHandler() },
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
 * A 4xx is the backend's verdict on a request the user made, and status 0 is a request that
 * was cancelled or never left the browser. Neither says anything about a frontend bug; real
 * server failures (5xx) still get reported, and the backend reports its own side of them.
 */
function isRequestNoise(exception: unknown): boolean {
  return exception instanceof HttpErrorResponse && exception.status < 500;
}
