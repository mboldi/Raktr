import { AbstractSecurityStorage } from 'angular-auth-oidc-client';
import {Injectable} from '@angular/core';

@Injectable()
export class LocalStorageService implements AbstractSecurityStorage {
  read(key: string): string | null {
    return localStorage.getItem(key);
  }

  write(key: string, value: string): void {
    localStorage.setItem(key, value);
  }

  remove(key: string): void {
    localStorage.removeItem(key);
  }

  clear(): void {
    localStorage.clear();
  }
}
