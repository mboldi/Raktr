import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAuth } from 'angular-auth-oidc-client';

import { MainLayoutComponent } from './main-layout.component';

// sidebarOpen and toggleSidebar are protected; the template drives them, and so
// does this test.
interface MainLayoutProbe {
  sidebarOpen: boolean;
  toggleSidebar(): void;
}

describe('MainLayoutComponent', () => {
  let component: MainLayoutComponent;
  let fixture: ComponentFixture<MainLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainLayoutComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        provideAuth({ config: { authority: 'http://localhost' } }),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MainLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('closes the sidebar on Escape', () => {
    const probe = component as unknown as MainLayoutProbe;

    probe.toggleSidebar();
    expect(probe.sidebarOpen).toBe(true);

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', bubbles: true }));
    fixture.detectChanges();

    expect(probe.sidebarOpen).toBe(false);
  });
});
