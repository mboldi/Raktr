// Tests run in jsdom, which lacks some browser APIs the app relies on. Stub them just enough
// for services like WindowWidthService to construct; tests that care should mock them.

if (!('ResizeObserver' in globalThis)) {
  globalThis.ResizeObserver = class {
    observe(): void {
      // no-op
    }
    unobserve(): void {
      // no-op
    }
    disconnect(): void {
      // no-op
    }
  };
}
