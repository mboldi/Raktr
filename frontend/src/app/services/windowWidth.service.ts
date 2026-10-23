import { Injectable, signal, computed, OnDestroy } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class WindowWidthService implements OnDestroy {
  readonly windowWidth = signal<number>(window.innerWidth);
  /** Same breakpoint at which the main layout switches to its mobile (slide-in menu) mode. */
  readonly isMobile = computed(() => this.windowWidth() < 992);

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
