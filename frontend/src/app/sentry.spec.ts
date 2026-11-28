import { sentryOptions } from './sentry';

describe('sentryOptions', () => {
  const config = {
    sentryDsn: 'https://key@sentry.example/1',
    sentryEnvironment: 'production',
    release: 'raktr-frontend@1.4.0',
  };

  it('reports the DSN, environment and release from the runtime config', () => {
    expect(sentryOptions(config)).toMatchObject({
      dsn: 'https://key@sentry.example/1',
      environment: 'production',
      release: 'raktr-frontend@1.4.0',
    });
  });

  it('traces navigation at 0.3', () => {
    const options = sentryOptions(config);

    expect(options.tracesSampleRate).toBe(0.3);
    expect(options.integrations).toEqual(
      expect.arrayContaining([expect.objectContaining({ name: 'BrowserTracing' })]),
    );
  });

  it('collects no personal data', () => {
    const privateHeaders = { deny: ['forwarded', '-ip', 'remote-', 'via', '-user'] };

    expect(sentryOptions(config).dataCollection).toEqual({
      cookies: false,
      databaseQueryData: false,
      genAI: { inputs: false, outputs: false },
      graphQL: { document: false, variables: false },
      httpBodies: [],
      httpHeaders: { request: privateHeaders, response: privateHeaders },
      urlQueryParams: false,
      userInfo: false,
    });
  });
});
