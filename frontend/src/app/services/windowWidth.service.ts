import { Injectable, signal, OnDestroy } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class WindowWidthService implements OnDestroy {
  readonly windowWidth = signal<number>(window.innerWidth);

  private readonly resizeObserver = new ResizeObserver(() => {
    this.windowWidth.set(window.innerWidth);
  });

  constructor() {
    this.resizeObserver.observe(document.documentElement);
  }

  ngOnDestroy(): void {
    this.resizeObserver.disconnect();
  }
}
