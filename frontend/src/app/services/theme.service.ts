import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  setDark(isDark: boolean) {
    document.documentElement.style.colorScheme = isDark ? 'dark' : 'light';
  }
}
