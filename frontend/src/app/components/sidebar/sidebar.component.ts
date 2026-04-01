import {Component, OnInit} from '@angular/core';
import {OidcSecurityService} from 'angular-auth-oidc-client';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {MatDivider, MatListItem, MatNavList} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {MatSlideToggle, MatSlideToggleChange} from '@angular/material/slide-toggle';
import {ThemeService} from '../../services/theme.service';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from '@angular/material/expansion';

declare const $: any;

declare interface RouteInfo {
  path: string;
  title: string;
  icon: string;
  class: string;
  inMenuBar: boolean;
  children?: RouteInfo[];
}

export const ROUTES: RouteInfo[] = [
  {path: '/overview', title: 'Áttekintés', icon: 'dashboard', class: '', inMenuBar: true, children: []},
  {path: '/inventory', title: 'Eszközök kezelése', icon: 'sd_storage', class: '', inMenuBar: true, children: [
      {path: '/devices', title: "Eszközök", icon: 'sd_storage', class: '', inMenuBar: true, children: []},
      {path: '/compositeitems', title: "Összetett eszközök", icon: 'inventory_2', class: '', inMenuBar: true, children: []},
      {path: '/containers', title: "Szállítóládák", icon: 'pallet', class: '', inMenuBar: true, children: []},
    ]},
  {path: '/rents', title: 'Kivitelek kezelése', icon: 'local_shipping', class: '', inMenuBar: true, children: []},
  {path: '/tickets', title: 'Hibajegyek', icon: 'bug_report', class: '', inMenuBar: true, children: []},
  {path: '/settings', title: 'Beállítások', icon: 'person', class: '', inMenuBar: true, children: []},
];

@Component({
  selector: 'app-sidebar',
  imports: [
    RouterLink,
    RouterLinkActive,
    MatDivider,
    MatIcon,
    MatNavList,
    MatListItem,
    MatSlideToggle,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
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

  isActiveParent(menuItem: RouteInfo): boolean {
    return menuItem.children?.some(child =>
      this.router.isActive(child.path, false)
    ) ?? false;
  }

  protected logout() {
    this.oidcSecurityService.logoff().subscribe();
    this.router.navigateByUrl('/login');
  }

  protected toggleDarkMode($event: MatSlideToggleChange) {
    this.themeService.setDark($event.checked)
  }
}
