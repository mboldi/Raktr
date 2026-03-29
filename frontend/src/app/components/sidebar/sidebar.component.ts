import {Component, OnInit} from '@angular/core';
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from '@angular/material/sidenav';
import {OidcSecurityService} from 'angular-auth-oidc-client';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {MatDivider, MatListItem, MatNavList} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {MatSlideToggle, MatSlideToggleChange} from '@angular/material/slide-toggle';
import {ThemeService} from '../../services/theme.service';

declare const $: any;

declare interface RouteInfo {
  path: string;
  title: string;
  icon: string;
  class: string;
  inMenuBar: boolean;
}

export const ROUTES: RouteInfo[] = [
  {path: '/overview', title: 'Áttekintés', icon: 'dashboard', class: '', inMenuBar: true},
  {path: '/devices', title: 'Eszközök kezelése', icon: 'sd_storage', class: '', inMenuBar: true},
  {path: '/compositeItems', title: 'Összetett eszközök kezelése', icon: 'sd_storage', class: '', inMenuBar: false},
  {path: '/rents', title: 'Kivitelek kezelése', icon: 'local_shipping', class: '', inMenuBar: true},
  {path: '/tickets', title: 'Hibajegyek', icon: 'bug_report', class: '', inMenuBar: true},
  {path: '/settings', title: 'Beállítások', icon: 'person', class: '', inMenuBar: true},
];

@Component({
  selector: 'app-sidebar',
  imports: [
    MatSidenavContainer,
    MatSidenavContent,
    RouterLink,
    RouterLinkActive,
    MatSidenav,
    RouterOutlet,
    MatDivider,
    MatIcon,
    MatNavList,
    MatListItem,
    MatSlideToggle
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent implements OnInit {

  menuItems: any[] = [];

  isDark: boolean = false;

  constructor(private oidcSecurityService: OidcSecurityService,
              private router: Router,
              private themeService: ThemeService) {
    this.isDark = this.themeService.isDark()
  }

  ngOnInit() {
    this.menuItems = ROUTES.filter(menuItem => menuItem);
  }

  isMobileMenu() {
    return $(window).width() <= 991;
  };

  protected logout() {
    this.oidcSecurityService.logoff().subscribe();
    this.router.navigateByUrl('/login');
  }

  protected toggleDarkMode($event: MatSlideToggleChange) {
    this.themeService.setDark($event.checked)
  }
}
