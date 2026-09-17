import {Injectable} from '@angular/core';

const THEME_KEY = 'app-theme';

@Injectable({providedIn: 'root'})
export class ThemeService {
  private readonly dark = 'dark';
  private readonly light = 'light';

  constructor() {
    this.loadTheme();
  }

  setDark(isDark: boolean): void {
    const theme = isDark ? this.dark : this.light;
    document.documentElement.style.colorScheme = theme;
    localStorage.setItem(THEME_KEY, theme);
  }

  isDark(): boolean {
    return localStorage.getItem(THEME_KEY) === this.dark;
  }

  private loadTheme(): void {
    const saved = localStorage.getItem(THEME_KEY);
    const theme = saved ?? this.light;

    if (!saved) {
      localStorage.setItem(THEME_KEY, theme);
    }

    document.documentElement.style.colorScheme = theme;
  }
}
