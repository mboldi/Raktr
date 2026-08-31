import {Component, computed, effect} from '@angular/core';
import {SidebarComponent} from '../../components/sidebar/sidebar.component';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {filter} from 'rxjs';
import {WindowWidthService} from '../../services/windowWidth.service';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

const MOBILE_WIDTH_BREAKPOINT = 992;

@Component({
  selector: 'app-main-layout',
  imports: [
    SidebarComponent,
    RouterOutlet,
    MatIconButton,
    MatIcon
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {

  protected sidebarOpen = false;
  protected isMobile = computed(() => this.windowService.windowWidth() < MOBILE_WIDTH_BREAKPOINT);

  constructor(
    private windowService: WindowWidthService,
    private router: Router,
  ) {
    effect(() => {
      if (!this.isMobile()) {
        this.sidebarOpen = false;
      }
    });

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => this.sidebarOpen = false);
  }

  protected toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  protected closeSidebar() {
    this.sidebarOpen = false;
  }
}
